package com.auth2client.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * 获取当前用户
     * @param authentication
     * @return
     */
    @RequestMapping("/getUserInfo")
    public Object getUserInfo(Authentication authentication){
        return authentication;
    }
}
