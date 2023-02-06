package com.intellif.service;

import com.intellif.dto.TokenParamDto;

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

    /**
     * 更新token有效期
     * @param tokenParamDto
     */
    void refreshToken(TokenParamDto tokenParamDto);

}
