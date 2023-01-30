package com.example.config.oauth;

import com.alibaba.fastjson2.JSONObject;
import com.example.config.PropertyBasedInterfaceMarshal;
import com.example.entity.SysUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.*;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 重写RedisTokenStore的逻辑，解决RedisTokenStore的序列化问题。
 * 解决过程：把原来的RedisTokenStore的序列化操作去除掉了
 * 而是使用json格式来直接存储
 *
 * @author Liangzhifeng
 * date: 2018/10/29
 */
@Slf4j
public class CustomRedisTokenStore implements TokenStore {

    private static final String ACCESS = "access:";
    private static final String AUTH_TO_ACCESS = "auth_to_access:";
    public static final String AUTH = "auth:";
    private static final String REFRESH_AUTH = "refresh_auth:";
    private static final String ACCESS_TO_REFRESH = "access_to_refresh:";
    private static final String REFRESH = "refresh:";
    private static final String REFRESH_TO_ACCESS = "refresh_to_access:";
    private static final String CLIENT_ID_TO_ACCESS = "client_id_to_access:";
    private static final String UNAME_TO_ACCESS = "uname_to_access:";


    private AuthenticationKeyGenerator authenticationKeyGenerator = new CustomAuthenticationKeyGenerator();
    private RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();

    private StringRedisTemplate redisTemplate;

    /**
     * 解决Gson反序列化接口时的bug
     */
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(OAuth2AccessToken.class, new PropertyBasedInterfaceMarshal())
            .registerTypeAdapter(Authentication.class, new PropertyBasedInterfaceMarshal())
            .registerTypeAdapter(OAuth2RefreshToken.class, new PropertyBasedInterfaceMarshal())
            .registerTypeAdapter(GrantedAuthority.class, new PropertyBasedInterfaceMarshal())
            .registerTypeAdapter(UserDetailsService.class, new PropertyBasedInterfaceMarshal())
            .create();

    private OAuth2Authentication deserializeAuthentication(byte[] bytes) {
        return (OAuth2Authentication)this.serializationStrategy.deserialize(bytes, OAuth2Authentication.class);
    }

    public CustomRedisTokenStore(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setAuthenticationKeyGenerator(AuthenticationKeyGenerator authenticationKeyGenerator) {
        this.authenticationKeyGenerator = authenticationKeyGenerator;
    }

    public StringRedisTemplate getRedisTemplate() {
        return this.redisTemplate;
    }

    /**
     * 获取授权token信息
     *
     * @param authentication
     * @return
     */
    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        String key = authenticationKeyGenerator.extractKey(authentication);
        byte[] serializedKey = (AUTH_TO_ACCESS + key).getBytes();
        return redisTemplate.execute((RedisCallback<OAuth2AccessToken>) connection -> {
            try {
                byte[] bytes = connection.get(serializedKey);
                if (bytes == null) {
                    return null;
                }

                String tokenJson = new String(bytes);
                if (StringUtils.isBlank(tokenJson)) {
                    return null;
                }

                OAuth2AccessToken accessToken = gson.fromJson(tokenJson, new TypeToken<OAuth2AccessToken>() {}.getType());
                if (accessToken != null && !key.equals(authenticationKeyGenerator.extractKey(readAuthentication(accessToken.getValue())))) {

                    storeAccessToken(accessToken, authentication);
                }
                // 添加增强信息 返回
//                if (accessToken != null){
//                    Map<String, Object> additionalInformation = accessToken.getAdditionalInformation();
//                    UserInfoRes resultUser = new UserInfoRes();
//                    resultUser = gson.fromJson(JSONObject.toJSONString(additionalInformation.get(GlobalConsts.USER_INFO_KEY)),UserInfoRes.class);
//                    additionalInformation.put(GlobalConsts.USER_INFO_KEY,resultUser);
//                }

                return accessToken;
            } catch (Exception e) {
                return null;
            } finally {
                connection.close();
            }
        });

    }

    /**
     * 根据token获取授权信息
     *
     * @param token
     * @return
     */
    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    /**
     * 根据token获取授权信息
     *
     * @param token
     * @return
     */
    @Override
    public OAuth2Authentication readAuthentication(String token) {
        return getOAuth2Authentication(AUTH + token);
    }

    /**
     * 根据refreshToken获取授权信息
     *
     * @param token
     * @return
     */
    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return readAuthenticationForRefreshToken(token.getValue());
    }

    /**
     * 根据refreshToken获取授权信息
     *
     * @param token
     * @return
     */
    public OAuth2Authentication readAuthenticationForRefreshToken(String token) {
        return getOAuth2Authentication(REFRESH_AUTH + token);
    }

    /**
     * 获取授权信息
     *
     * @param key
     * @return
     */
    private OAuth2Authentication getOAuth2Authentication(String key) {
        return redisTemplate.execute((RedisCallback<OAuth2Authentication>) connection -> {
            try {
                byte[] bytes = connection.get(key.getBytes());
                if (bytes == null || bytes.length == 0) {
                    return null;
                }
                OAuth2Authentication auth = gson.fromJson(new String(bytes), new TypeToken<OAuth2Authentication>() {}.getType());
                // // 添加增强信息 返回
//                String json = gson.toJson(auth.getPrincipal());
//                UserOAuthDetail userOAuthDetail = gson.fromJson(json, UserOAuthDetail.class);
//                Authentication authentication = auth.getUserAuthentication();
//                if (authentication instanceof UsernamePasswordAuthenticationToken) {
//                    setUserData(authentication, userOAuthDetail);
//                }
                return auth;
            } catch (Exception e) {
                log.error("获取登录授权信息异常key={}", key, e);
                return null;
            } finally {
                connection.close();
            }

        });
    }

//    private void setUserData(Authentication authentication, UserOAuthDetail userOAuthDetail) {
//        try {
//            Class<?> clazz = Class.forName(authentication.getClass().getName());
//            Field principal = clazz.getDeclaredField("principal");
//            principal.setAccessible(true);
//            principal.set(authentication, userOAuthDetail);
//        } catch (Exception e) {
//            log.error("解析授权信息异常", e);
//        }
//    }

    /**
     * 存储token信息以及授权信息
     *
     * @param token          token信息
     * @param authentication 授权信息
     */
    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {

        redisTemplate.execute((RedisCallback<Boolean>) connection -> {

            byte[] serializedAccessToken = gson.toJson(token, new TypeToken<OAuth2AccessToken>() {}.getType()).getBytes();
            byte[] serializedAuth = gson.toJson(authentication, new TypeToken<OAuth2Authentication>() {}.getType()).getBytes();
            byte[] accessKey = (ACCESS + token.getValue()).getBytes();
            byte[] authKey = (AUTH + token.getValue()).getBytes();
            byte[] authToAccessKey = (AUTH_TO_ACCESS + authenticationKeyGenerator.extractKey(authentication)).getBytes();
            byte[] approvalKey = (UNAME_TO_ACCESS + getApprovalKey(authentication)).getBytes();
            byte[] clientId = (CLIENT_ID_TO_ACCESS + authentication.getOAuth2Request().getClientId()).getBytes();

            try {
                connection.openPipeline();
                connection.set(accessKey, serializedAccessToken);
                connection.set(authKey, serializedAuth);
                connection.set(authToAccessKey, serializedAccessToken);
                if (!authentication.isClientOnly()) {
                    connection.sAdd(approvalKey, serializedAccessToken);
                }
                connection.sAdd(clientId, serializedAccessToken);

                if (token.getExpiration() != null) {
                    int seconds = token.getExpiresIn();
                    connection.expire(accessKey, seconds);
                    connection.expire(authKey, seconds);
                    connection.expire(authToAccessKey, seconds);
                    connection.expire(clientId, seconds);
                    connection.expire(approvalKey, seconds);
                }

                OAuth2RefreshToken refreshToken = token.getRefreshToken();
                if (refreshToken != null && refreshToken.getValue() != null) {
                    byte[] refresh = (token.getRefreshToken().getValue()).getBytes();
                    byte[] auth = (token.getValue()).getBytes();
                    byte[] refreshToAccessKey = (REFRESH_TO_ACCESS + token.getRefreshToken().getValue()).getBytes();
                    byte[] accessToRefreshKey = (ACCESS_TO_REFRESH + token.getValue()).getBytes();

                    connection.set(refreshToAccessKey, auth);
                    connection.set(accessToRefreshKey, refresh);

                    doSetExpired(refreshToken, connection, refreshToAccessKey, accessToRefreshKey);
                }
                connection.closePipeline();
            } finally {
                connection.close();
            }

            return true;
        });
    }

    /**
     * 生成授权信息的key
     *
     * @param authentication
     * @return
     */
    private static String getApprovalKey(OAuth2Authentication authentication) {
        String userName = "";
        if (authentication.getUserAuthentication() != null){
            SysUser sysUser = JSONObject.parseObject(JSONObject.toJSONString(authentication.getUserAuthentication().getPrincipal()),SysUser.class);
            userName = sysUser.getUsername();
        }

        return getApprovalKey(authentication.getOAuth2Request().getClientId(), userName);
    }

    private static String getApprovalKey(String clientId, String userName) {
        return clientId + (userName == null ? "" : ":" + userName);
    }

    /**
     * 移除token信息
     *
     * @param accessToken
     */
    @Override
    public void removeAccessToken(OAuth2AccessToken accessToken) {
        removeAccessToken(accessToken.getValue());
    }

    /**
     * 根据token值获取token信息
     *
     * @param tokenValue
     * @return
     */
    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {

        return redisTemplate.execute((RedisCallback<OAuth2AccessToken>) connection -> {
            try {
                byte[] key = (ACCESS + tokenValue).getBytes();
                byte[] bytes = connection.get(key);
                if (bytes == null) {
                    return null;
                }

                String tokenJson = new String(bytes);
                if (StringUtils.isBlank(tokenJson)) {
                    return null;
                }

                return gson.fromJson(tokenJson, new TypeToken<OAuth2AccessToken>() {}.getType());
            } catch (Exception e) {
                log.error("获取登录token信息异常tokenValue={}", tokenValue, e);
                return null;
            } finally {
                connection.close();
            }
        });
    }

    /**
     * 删除token相关信息
     *
     * @param tokenValue
     */
    public void removeAccessToken(String tokenValue) {

        redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            byte[] accessKey = (ACCESS + tokenValue).getBytes();
            byte[] authKey = (AUTH + tokenValue).getBytes();
            byte[] accessToRefreshKey = (ACCESS_TO_REFRESH + tokenValue).getBytes();

            try {
                connection.openPipeline();

                connection.get(accessKey);
                connection.get(authKey);
                connection.del(accessKey);
                connection.del(accessToRefreshKey);
                connection.del(authKey);
                List<Object> results = connection.closePipeline();
                byte[] access = (byte[]) results.get(0);
                byte[] auth = (byte[]) results.get(1);

                String s = new String(auth);
                OAuth2Authentication authentication = gson.fromJson(s, new TypeToken<OAuth2Authentication>() {}.getType());
                if (authentication != null) {
                    String key = authenticationKeyGenerator.extractKey(authentication);

                    byte[] authToAccessKey = (AUTH_TO_ACCESS + key).getBytes();
                    byte[] uNameKey = (UNAME_TO_ACCESS + getApprovalKey(authentication)).getBytes();
                    byte[] clientId = (CLIENT_ID_TO_ACCESS + authentication.getOAuth2Request().getClientId()).getBytes();

                    connection.openPipeline();
                    connection.del(authToAccessKey);
                    connection.sRem(uNameKey, access);
                    connection.sRem(clientId, access);
                    connection.del((ACCESS + key).getBytes());
                    connection.closePipeline();
                }
            } finally {
                connection.close();
            }
            return true;
        });

    }

    /**
     * 存储refreshToken,以及授权信息
     *
     * @param refreshToken
     * @param authentication
     */
    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {

        redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            byte[] refreshKey = (REFRESH + refreshToken.getValue()).getBytes();
            byte[] refreshAuthKey = (REFRESH_AUTH + refreshToken.getValue()).getBytes();
            byte[] serializedRefreshToken = gson.toJson(refreshToken, new TypeToken<OAuth2RefreshToken>() {}.getType()).getBytes();

            try {
                connection.openPipeline();

                connection.set(refreshKey, serializedRefreshToken);
                connection.set(refreshAuthKey, gson.toJson(authentication, new TypeToken<OAuth2Authentication>() {}.getType()).getBytes());

                doSetExpired(refreshToken, connection, refreshKey, refreshAuthKey);
                connection.closePipeline();
            } finally {
                connection.close();
            }
            return true;
        });

    }

    /**
     * 根据token值获取refreshToken信息
     *
     * @param tokenValue
     * @return
     */
    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {

        return redisTemplate.execute((RedisCallback<OAuth2RefreshToken>) connection -> {
            byte[] key = (REFRESH + tokenValue).getBytes();
            try {
                byte[] bytes = connection.get(key);
                if (bytes == null) {
                    return null;
                }

                String tokenJson = new String(bytes);
                if (StringUtils.isBlank(tokenJson)) {
                    return null;
                }
                return gson.fromJson(tokenJson, new TypeToken<OAuth2RefreshToken>() {}.getType());
            } finally {
                connection.close();
            }

        });
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken refreshToken) {
        removeRefreshToken(refreshToken.getValue());
    }

    public void removeRefreshToken(String tokenValue) {
        redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            byte[] refreshKey = (REFRESH + tokenValue).getBytes();
            byte[] refreshAuthKey = (REFRESH_AUTH + tokenValue).getBytes();
            byte[] refresh2AccessKey = (REFRESH_TO_ACCESS + tokenValue).getBytes();
            byte[] access2RefreshKey = (ACCESS_TO_REFRESH + tokenValue).getBytes();
            try {
                connection.openPipeline();
                connection.del(refreshKey);
                connection.del(refreshAuthKey);
                connection.del(refresh2AccessKey);
                connection.del(access2RefreshKey);

                connection.closePipeline();
            } finally {
                connection.close();
            }
            return true;
        });

    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        removeAccessTokenUsingRefreshToken(refreshToken.getValue());
    }

    private void removeAccessTokenUsingRefreshToken(String refreshToken) {

        redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            byte[] key = (REFRESH_TO_ACCESS + refreshToken).getBytes();
            List<Object> results;
            try {
                connection.openPipeline();
                connection.get(key);
                connection.del(key);
                results = connection.closePipeline();
            } finally {
                connection.close();
            }

            if (!CollectionUtils.isEmpty(results)) {
                byte[] bytes = (byte[]) results.get(0);
                String accessToken = new String(bytes);
                removeAccessToken(accessToken);
            }

            return true;
        });
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        return redisTemplate.execute((RedisCallback<Collection<OAuth2AccessToken>>) connection -> {

            byte[] approvalKey = (UNAME_TO_ACCESS + getApprovalKey(clientId, userName)).getBytes();
            return getOAuth2AccessTokens(connection, approvalKey);
        });
    }

    /**
     * 根据授权的客户端
     *
     * @param clientId
     * @return
     */
    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        return redisTemplate.execute((RedisCallback<Collection<OAuth2AccessToken>>) connection -> {

            byte[] key = (CLIENT_ID_TO_ACCESS + clientId).getBytes();
            return getOAuth2AccessTokens(connection, key);
        });
    }

    private Collection<OAuth2AccessToken> getOAuth2AccessTokens(RedisConnection connection, byte[] key) {
        List<byte[]> byteList;
        try {
            byteList = this.getByteLists(key, connection);
        } finally {
            connection.close();
        }
        if (CollectionUtils.isEmpty(byteList)) {
            return Collections.emptySet();
        }
        return collectionTokenFromBytes(byteList);
    }

    /**
     * 反序列化 OAuth2AccessToken
     *
     * @param byteList
     * @return
     */
    private Collection<OAuth2AccessToken> collectionTokenFromBytes(List<byte[]> byteList) {
        List<OAuth2AccessToken> accessTokens = new ArrayList<>(byteList.size());
        for (byte[] bytes : byteList) {
            if (bytes != null) {
                String tokenJson = new String(bytes);
                if (StringUtils.isNotBlank(tokenJson)) {
                    OAuth2AccessToken accessToken = gson.fromJson(tokenJson, new TypeToken<OAuth2AccessToken>() {}.getType());
                    accessTokens.add(accessToken);
                }
            }

        }
        return Collections.unmodifiableCollection(accessTokens);
    }

    private List<byte[]> getByteLists(byte[] approvalKey, RedisConnection conn) {
        Long size = conn.sCard(approvalKey);
        List<byte[]> byteList = new ArrayList(size.intValue());
        Cursor cursor = conn.sScan(approvalKey, ScanOptions.NONE);

        while(cursor.hasNext()) {
            byteList.add((byte[]) cursor.next());
        }

        return byteList;
    }

    /**
     * 设置refreshToken对应的key的过期时间
     *
     * @param refreshToken
     * @param connection
     * @param keyList
     */
    private void doSetExpired(OAuth2RefreshToken refreshToken, RedisConnection connection, byte[]... keyList) {
        if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
            ExpiringOAuth2RefreshToken expiringRefreshToken = (ExpiringOAuth2RefreshToken) refreshToken;
            Date expiration = expiringRefreshToken.getExpiration();
            if (expiration != null) {
                int seconds = Long.valueOf((expiration.getTime() - System.currentTimeMillis()) / 1000).intValue();
                if (keyList != null) {
                    for (byte[] bytes : keyList) {
                        connection.expireAt(bytes, seconds);
                    }
                }
            }
        }
    }


    /**
     * 刷新登录时间
     *
     * @param token       登录授权的token
     * @param expiredTime 新的登录过期时间
     * @return 重新设置登录过期时间是否成功
     */
    public boolean refreshLoginExpired(String token, long expiredTime) {
        OAuth2AccessToken accessToken = readAccessToken(token);
        if (accessToken == null) {
            return false;
        }
        OAuth2Authentication authentication = readAuthentication(accessToken);
        if (authentication == null) {
            return false;
        }

        byte[] accessKey = (ACCESS + token).getBytes();
        byte[] authKey = (AUTH + token).getBytes();
        byte[] authToAccessKey = (AUTH_TO_ACCESS + authenticationKeyGenerator.extractKey(authentication)).getBytes();
//        byte[] approvalKey = (UNAME_TO_ACCESS + getApprovalKey(authentication)).getBytes();
//        byte[] clientId = (CLIENT_ID_TO_ACCESS + authentication.getOAuth2Request().getClientId()).getBytes();

        //重新设置过期时间
        Date newExpiration = new Date(System.currentTimeMillis() + expiredTime * 1000);
        OAuth2RefreshToken refreshToken = accessToken.getRefreshToken();
        if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
            ExpiringOAuth2RefreshToken newRefreshToken = new DefaultExpiringOAuth2RefreshToken(refreshToken.getValue(), newExpiration);
            ((DefaultOAuth2AccessToken) accessToken).setRefreshToken(newRefreshToken);
        }
        ((DefaultOAuth2AccessToken) accessToken).setExpiration(newExpiration);
        byte[] serializedAccessToken = gson.toJson(accessToken, new TypeToken<OAuth2AccessToken>() {
        }.getType()).getBytes();
        return redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            try {
                connection.openPipeline();
                connection.set(accessKey, serializedAccessToken);
                connection.expire(accessKey, expiredTime);
                connection.expire(authKey, expiredTime);
                connection.expire(authToAccessKey, expiredTime);
//                connection.expire(clientId, expiredTime);
//                connection.expire(approvalKey, expiredTime);

                if (refreshToken != null && refreshToken.getValue() != null) {
                    byte[] refreshToAccessKey = (REFRESH_TO_ACCESS + accessToken.getRefreshToken().getValue()).getBytes();
                    byte[] accessToRefreshKey = (ACCESS_TO_REFRESH + accessToken.getValue()).getBytes();
                    if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
                        connection.expire(refreshToAccessKey, expiredTime);
                        connection.expire(accessToRefreshKey, expiredTime);
                    }
                }
                connection.closePipeline();
                return true;
            } catch (Exception e) {
                log.error("更新登录时间异常token={}, expiredTime={}", token, expiredTime, e);
                return false;
            } finally {
                connection.close();
            }
        });
    }

    /**
     * 获取授权信息的唯一key
     *
     * @param authentication
     * @return
     */
    public String getOAuth2AuthenticationKey(OAuth2Authentication authentication) {
        return authenticationKeyGenerator.extractKey(authentication);
    }


    /**
     * 判断redis中是否存在改token
     *
     * @param token
     * @return
     */
    public boolean redisHaveToken(String token) {
        return redisTemplate.hasKey(token);
    }
}
