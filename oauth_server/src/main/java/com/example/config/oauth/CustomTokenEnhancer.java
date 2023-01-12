package com.example.config.oauth;

import com.example.entity.SysUser;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * jwt内容增强器
 * Created by YangGuanRong
 * date: 2023/1/6
 */

@Slf4j
public class CustomTokenEnhancer implements TokenEnhancer {

    private StringRedisTemplate stringRedisTemplate;


    public CustomTokenEnhancer(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        // 增强token内容
        Map<String,Object> info = new HashMap<>();
        info.put("enhancer","enhance info");
        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(info);

        long expireTime = oAuth2AccessToken.getExpiration().getTime() - System.currentTimeMillis();

        // 把token保存到redis
        SysUser user = (SysUser) oAuth2Authentication.getPrincipal();
        String token = oAuth2AccessToken.getValue();
        Gson gson = new Gson();
        String data = gson.toJson(user);
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        Boolean handle = valueOperations.setIfAbsent(token, data);
        log.info("存储用户登录信息，账号：{}，token={},success={}", user.getUsername(),token, handle);
        if (handle) {
            //存储用户和token的关系
            OAuth2Request authorizationRequest = oAuth2Authentication.getOAuth2Request();
            String key = user.getUsername() + "_" + 0L + "_" +authorizationRequest.getClientId();
            valueOperations.set(key, token, expireTime, TimeUnit.MILLISECONDS);
            //存储token对应的用户信息
            valueOperations.getOperations().expire(token, expireTime, TimeUnit.MILLISECONDS);
        }
        return oAuth2AccessToken;
    }
}
