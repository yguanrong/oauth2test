package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.config.LoginEnvConfig;
import com.example.config.oauth.CustomRedisTokenStore;
import com.example.dto.TokenParamDto;
import com.example.dto.UserInfo;
import com.example.entity.OauthClientDetails;
import com.example.entity.SysUser;
import com.example.mapper.OauthClientDetailsMapper;
import com.example.service.LoginService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author Liangzhifeng
 * date: 2018/7/31
 */
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    LoginEnvConfig loginEnvConfig;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    CustomRedisTokenStore redisTokenStore;

    @Autowired
    OauthClientDetailsMapper oauthClientDetailsMapper;

    @Override
    public void logout(String token) {

        String s = redisTemplate.opsForValue().get(token);
        Gson gson = new Gson();
        UserInfo userInfo = gson.fromJson(s, UserInfo.class);

        //删除token
        redisTokenStore.removeAccessToken(token);

        //删除redis缓存的用户信息
        String userName_clientId = userInfo.getUserVo().getUsername() + "_" + 0L + "_" + userInfo.getUserVo().getClientId();
        redisTemplate.delete(userName_clientId);

        redisTemplate.delete(token);

    }

    @Override
    public void refreshToken(TokenParamDto tokenParamDto) {
        if (StringUtils.isNotEmpty(tokenParamDto.getToken())) {
            String s = redisTemplate.opsForValue().get(tokenParamDto.getToken());
            Gson gson = new Gson();
            UserInfo userInfo = gson.fromJson(s, UserInfo.class);

            String username = userInfo.getUserVo().getUsername();
            String clientId = userInfo.getUserVo().getClientId();

            // 获取clientId对应的token有效期
            LambdaQueryWrapper<OauthClientDetails> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OauthClientDetails::getClientId,clientId);
            OauthClientDetails oauthClientDetails = oauthClientDetailsMapper.selectOne(queryWrapper);

            long tokenExpireTime = oauthClientDetails.getAccessTokenValidity();
            //重新设置登录授权的过期时间
            boolean resetLoginExpired = redisTokenStore.refreshLoginExpired(tokenParamDto.getToken(), tokenExpireTime);
            if (resetLoginExpired) {
                //重新设置redis的用户信息过期时间
                String userName_clientId = username + "_" + 0L + "_" + clientId;
                redisTemplate.expire(userName_clientId, tokenExpireTime, TimeUnit.SECONDS);
                redisTemplate.expire(tokenParamDto.getToken(), tokenExpireTime, TimeUnit.SECONDS);
            }
        }
    }

}
