package com.example.dto;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

/**
 * @author Liangzhifeng
 * date: 2018/7/31
 */
@Data
public class UserRoleInfo implements GrantedAuthority, Serializable {

    private static final long serialVersionUID = 6500913929801272205L;
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色中文名
     */
    private String roleCnName;

    /**
     * 角色类型
     */
    private Integer type;

    /**
     * 角色组织id
     */
    private Long orgId;

    /**
     * 角色等级
     */
    private Integer grade;

    /**
     * 操作权限
     */
    private String operateAuth;

    @Override
    public String getAuthority() {
        return String.valueOf(roleId);
    }
}
