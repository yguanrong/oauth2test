package com.example.config.oauth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * Created by YangGuanRong
 * date: 2023/1/6
 */
@Configuration
public class JwtTokenStoreConfig {

    @Value("${jwt.key:test_key}")
    private String jwtKey;

    @Bean("jwtTokenStore")
    @Primary
    public TokenStore jwtTokenStore(){
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
        // 配置jwt使用的秘钥
        accessTokenConverter.setSigningKey(jwtKey);
        return accessTokenConverter;
    }

    @Bean
    public JwtTokenEnhancer jwtTokenEnhancer(){
        return new JwtTokenEnhancer();
    }

}
