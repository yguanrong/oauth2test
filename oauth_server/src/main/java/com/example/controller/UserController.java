package com.example.controller;

import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;


/**
 * Created by YangGuanRong
 * date: 2023/1/3
 */
@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * 获取当前用户
     * @param authentication
     * @return
     */
    @GetMapping(value = "/getCurrentUser")
    public Object getCurrentUser(Authentication authentication, HttpServletRequest httpServletRequest) {
        String head = httpServletRequest.getHeader("Authorization");
        String token = head.substring(head.indexOf("bearer") + 7);

        return Jwts.parser()
                .setSigningKey("test_key".getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();
    }
}