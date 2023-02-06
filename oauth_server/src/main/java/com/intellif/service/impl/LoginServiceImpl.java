package com.intellif.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.intellif.config.oauth.CustomRedisTokenStore;
import com.intellif.dto.TokenParamDto;
import com.intellif.entity.OauthClientDetails;
import com.intellif.entity.SysUser;
import com.intellif.mapper.OauthClientDetailsMapper;
import com.intellif.service.LoginService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    private StringRedisTemplate redisTemplate;

    @Autowired
    CustomRedisTokenStore redisTokenStore;

    @Autowired
    OauthClientDetailsMapper oauthClientDetailsMapper;

    @Override
    public void logout(String token) {
        //删除token
        redisTokenStore.removeAccessToken(token);

        //删除redis缓存的用户信息
        redisTemplate.delete(token);

    }

    @Override
    public void refreshToken(TokenParamDto tokenParamDto) {
        if (StringUtils.isNotEmpty(tokenParamDto.getToken())) {
            String s = redisTemplate.opsForValue().get(tokenParamDto.getToken());
            Gson gson = new Gson();
            SysUser userInfo = gson.fromJson(s, SysUser.class);

            String clientId = userInfo.getClientId();

            // 获取clientId对应的token有效期
            LambdaQueryWrapper<OauthClientDetails> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OauthClientDetails::getClientId,clientId);
            OauthClientDetails oauthClientDetails = oauthClientDetailsMapper.selectOne(queryWrapper);

            long tokenExpireTime = oauthClientDetails.getAccessTokenValidity();
            //重新设置登录授权的过期时间
            boolean resetLoginExpired = redisTokenStore.refreshLoginExpired(tokenParamDto.getToken(), tokenExpireTime);
            if (resetLoginExpired) {
                //重新设置redis的用户信息过期时间
                redisTemplate.expire(tokenParamDto.getToken(), tokenExpireTime, TimeUnit.SECONDS);
            }
        }
    }

}
