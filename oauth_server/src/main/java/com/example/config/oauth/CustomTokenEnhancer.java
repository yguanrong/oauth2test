package com.example.config.oauth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.dto.Operation;
import com.example.dto.UserArea;
import com.example.dto.UserInfo;
import com.example.dto.UserRoleInfo;
import com.example.entity.SysUser;
import com.example.mapper.SysUserMapper;
import com.example.service.UserService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import javax.annotation.Resource;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * jwt内容增强器
 * Created by YangGuanRong
 * date: 2023/1/6
 */

@Slf4j
public class CustomTokenEnhancer implements TokenEnhancer {

    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    SysUserMapper userMapper;

    @Autowired
    UserService userService;


    public CustomTokenEnhancer(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        long expireTime = oAuth2AccessToken.getExpiration().getTime() - System.currentTimeMillis();

        // 获取用户授权信息
        UserInfo userInfo = getUserAuthInfo(oAuth2Authentication);

        // 把token：UserInfo 保存到redis
        storeTokenUserInfoToRedis(oAuth2AccessToken,oAuth2Authentication, expireTime, userInfo);

        // token增强器，增强内容只存在redis中，不返回到前端
        Map<String,Object> info = new HashMap<>();
        userInfo.setOperationList(null);
        info.put("userInfo",userInfo);
        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(info);

        return oAuth2AccessToken;
    }

    /**
     * 保存token和用户信息到redis
     * @param oAuth2Authentication
     * @param expireTime
     * @param userInfo
     */
    private void storeTokenUserInfoToRedis(OAuth2AccessToken oAuth2AccessToken,OAuth2Authentication oAuth2Authentication, long expireTime, UserInfo userInfo) {
        OAuth2Request authorizationRequest = oAuth2Authentication.getOAuth2Request();

        userInfo.getUserVo().setClientId(authorizationRequest.getClientId());
        String token = oAuth2AccessToken.getValue();
        Gson gson = new Gson();
        String data = gson.toJson(userInfo);
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        Boolean handle = valueOperations.setIfAbsent(token, data);
        log.info("存储用户登录信息，账号：{}，token={},success={}", userInfo.getUserVo().getUsername(), token, handle);

        if (handle!= null && handle) {
            //存储用户和token的关系
            String key = userInfo.getUserVo().getUsername() + "_" + 0L + "_" +authorizationRequest.getClientId();
            valueOperations.set(key, token, expireTime, TimeUnit.MILLISECONDS);
            //存储token对应的用户信息
            valueOperations.getOperations().expire(token, expireTime, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 获取用户权限模块等消息，等信息
     * @param oAuth2Authentication
     * @return
     */
    private UserInfo getUserAuthInfo(OAuth2Authentication oAuth2Authentication){
        SysUser user = (SysUser) oAuth2Authentication.getPrincipal();
        OAuth2Request authorizationRequest = oAuth2Authentication.getOAuth2Request();
        UserInfo userInfo = new UserInfo();
        userInfo.setUserVo(user);

        // 获取用户权限信息
        UserInfo authInfo = userService.getUserAuthInfo(user.getId(), authorizationRequest.getClientId());
        userInfo.setUserRoleInfo(authInfo.getUserRoleInfo());
        userInfo.setOauthResources(authInfo.getOauthResources());
        userInfo.setOperationList(authInfo.getOperationList());
        userInfo.setUserAreaList(authInfo.getUserAreaList());

        return userInfo;
    }

}
