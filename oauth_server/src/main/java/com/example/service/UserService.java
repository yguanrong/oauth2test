package com.example.service;

import com.example.dto.Operation;
import com.example.dto.UserArea;
import com.example.dto.UserInfo;
import com.example.dto.UserRoleInfo;
import com.example.entity.SysUser;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.swing.tree.TreeNode;
import java.util.List;


public interface UserService extends UserDetailsService {

    SysUser queryUserByPhone(String phone);

    /**
     * 返回当前平台的角色信息
     * @param userId 用户id
     * @param clientId 平台标识
     * @return
     */
    List<UserRoleInfo> getUserRoleInfo(long userId,String clientId);

    /**
     * 返回所有有资源的接口
     * @param userId 用户id
     * @param clientId 平台标识
     * @return
     */
    List<Operation> getOperationList(long userId, String clientId);

    /**
     * 直返当前平台登录的资源树
     * @param userId 用户id
     * @param clientId 平台标识
     * @return
     */
    List<TreeNode> getOauthResources(long userId,String clientId);

    /**
     * 返回当前平台的用户
     * @param userId 用户id
     * @param clientId 平台标识
     * @return
     */
    List<UserArea> getUserAreaList(long userId,String clientId);

    /**
     * 返回当前平台的用户资源信息
     * @param userId 用户id
     * @param clientId 平台标识
     * @return
     */
    UserInfo getUserAuthInfo(long userId, String clientId);

}
