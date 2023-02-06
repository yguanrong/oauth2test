package com.example.controller;

import com.example.dto.ServerResp;
import com.example.entity.SysUser;
import com.example.service.ISysUserService;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


/**
 * Created by YangGuanRong
 * date: 2023/1/3
 */
@RestController
@RequestMapping("/user")
@Api("UserController")
public class UserController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    ISysUserService sysUserService;

    /**
     * 获取当前用户
     * @return
     */
    @GetMapping(value = "/getCurrentUser")
    @ApiOperation(value = "获取当前用户信息",httpMethod = "GET")
    public ServerResp getCurrentUser(HttpServletRequest httpServletRequest) {
        String head = httpServletRequest.getHeader("token");
        String token = head.substring(head.indexOf("Bearer") + 7);
        String userData = redisTemplate.opsForValue().get(token);
        if (StringUtils.isEmpty(userData)) {
            return new ServerResp("获取用户信息失败",ServerResp.ERROR_CODE);
        }
        Gson gson = new Gson();
        SysUser user = gson.fromJson(userData,SysUser.class);
        return new ServerResp(user);
    }

    /**
     * 新增用户
     * @return
     */
    @PostMapping(value = "/create")
    @ApiOperation(value = "新增用户",httpMethod = "POST")
    public ServerResp create(@RequestBody SysUser sysUser) {
        SysUser user= sysUserService.create(sysUser);
        return new ServerResp(user);
    }

    /**
     * 删除用户
     * @return
     */
    @PostMapping(value = "/delete")
    @ApiOperation(value = "删除用户",httpMethod = "POST")
    public ServerResp delete(@RequestBody SysUser sysUser) {
        sysUserService.delete(sysUser);
        return new ServerResp();
    }

    /**
     * 编辑用户
     * @return
     */
    @PostMapping(value = "/update")
    @ApiOperation(value = "编辑用户",httpMethod = "POST")
    public ServerResp update(@RequestBody SysUser sysUser) {
        sysUserService.updateById(sysUser);
        return new ServerResp(sysUser);
    }
}
