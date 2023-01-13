package com.example.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 客户端记录表
 * </p>
 *
 * @author dufu
 * @since 2023-01-13
 */
@Data
@ApiModel("OauthClientDto")
public class OauthClientDto{

    @ApiModelProperty(value = "可选枚举值：password,refresh_token,authorization_code,sms_code",required = true,example = "password,refresh_token,authorization_code,sms_code")
    private String authorizedGrantTypes;

    @ApiModelProperty(value = "登录后跳转的url",required = true,example = "http://www.baidu.com")
    private String webServerRedirectUri;

    @ApiModelProperty(value = "token 过期的时间，单位 秒",required = true,example = "3600")
    private Integer accessTokenValidity;

    @ApiModelProperty(value = "refreshToken 过期的时间，单位 秒",required = true,example = "864000")
    private Integer refreshTokenValidity;

    @ApiModelProperty(value = "自动授权 true、false",required = true,example = "true")
    private String autoapprove;

}
