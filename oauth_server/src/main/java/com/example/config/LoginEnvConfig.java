package com.example.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by YangGuanRong
 * date: 2023/1/31
 */
@Component
@Data
public class LoginEnvConfig {

    /**
     * token过期时间，秒
     */
    @Value("${token.expire.time:3600}")
    long tokenExpireTime;
}
