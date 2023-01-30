package com.example.service.impl;

import com.example.config.oauth.CustomRedisTokenStore;
import com.example.dto.UserInfo;
import com.example.entity.SysUser;
import com.example.service.LoginService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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

    @Override
    public void logout(String token) {

        String s = redisTemplate.opsForValue().get(token);
        Gson gson = new Gson();
        UserInfo userInfo = gson.fromJson(s, UserInfo.class);

        //删除token
        redisTokenStore.removeAccessToken(token);

        //删除redis缓存的用户信息
        String key = userInfo.getUserVo().getUsername() + "_" + 0L + "_" + userInfo.getUserVo().getClientId();
        redisTemplate.delete(key);

        redisTemplate.delete(token);

    }
}
