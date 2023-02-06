package com.intellif.config;

import com.intellif.config.oauth.CustomRedisTokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

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
