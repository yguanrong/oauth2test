package com.example.config;

import com.example.config.oauth.CustomRedisTokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * Created by YangGuanRong
 * date: 2023/1/5
 */
@Configuration
public class RedisConfig {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Bean("redisTokenStore")
    public CustomRedisTokenStore redisTokenStore(){

        return new CustomRedisTokenStore(stringRedisTemplate);
    }

}
