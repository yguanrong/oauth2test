package com.example.controller;


import com.example.consts.ResourceUrlConsts;
import com.example.dto.ServerResp;
import com.example.dto.TokenParamDto;
import com.example.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录相关的接口
 * Created by YangGuanRong
 * date: 2023/1/30
 */
@RestController
@Api("LoginController")
@RequestMapping("/user")
public class LoginController {


    @Autowired
    LoginService loginService;

    /**
     * 退出登录
     * @return
     */
    @PostMapping(value = "/logout")
    @ApiOperation(value = "退出登录",httpMethod = "POST")
    public Object logout(HttpServletRequest request) {
        try {
            String authorization = request.getHeader("token");
            if (StringUtils.isBlank(authorization) || !authorization.contains(ResourceUrlConsts.AUTHORIZATION_BEARER)) {
                return new ServerResp("缺少授权信息,退出登录失败", ServerResp.ERROR_CODE);
            }

            String token = authorization.substring(ResourceUrlConsts.AUTHORIZATION_BEARER.length() + 1);
            if (StringUtils.isBlank(token)) {
                return new ServerResp("缺少授权信息,退出登录失败", ServerResp.ERROR_CODE);
            }
            loginService.logout(token);
            return new ServerResp();
        } catch (Exception e) {
            return new ServerResp("退出登录失败：" + e.getMessage(), ServerResp.ERROR_CODE);
        }
    }

    /**
     * 刷新token有效期
     * @return
     */
    @PostMapping(value = "/refreshToken")
    @ApiOperation(value = "刷新token有效期",httpMethod = "POST")
    public ServerResp refreshToken(@RequestBody TokenParamDto tokenParamDto) {
        try {
            loginService.refreshToken(tokenParamDto);
            return new ServerResp();
        } catch (Exception e) {
            return new ServerResp("退出登录失败：" + e.getMessage(), ServerResp.ERROR_CODE);
        }
    }

}
