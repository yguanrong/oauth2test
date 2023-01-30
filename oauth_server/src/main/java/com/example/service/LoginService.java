package com.example.service;

/**
 * 退出登录接口
 * @author Liangzhifeng
 * date: 2018/7/31
 */
public interface LoginService {

    /**
     * 退出登录
     * @param token
     */
    void logout(String token);
}
