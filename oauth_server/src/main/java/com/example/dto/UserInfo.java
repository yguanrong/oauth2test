package com.example.dto;

import com.example.entity.SysUser;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Liangzhifeng
 * date: 2018/7/31
 */
@Data
public class UserInfo implements Serializable {

    private static final long serialVersionUID = -6181746620261240272L;

    /**
     * 用户基础数据
     */
    private SysUser userVo;

    /**
     * 用户角色
     */
    private List<UserRoleInfo> userRoleInfo;

    /**
     * 有权限的接口列表
     */
    private List<Operation> operationList;

    /**
     * 有权限的资源树结构
     */
    private Object oauthResources;

    /**
     * 数据权限（用户和区域）
     */
    private List<UserArea> userAreaList;

}
