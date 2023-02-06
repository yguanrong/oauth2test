package com.example.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.consts.ResourceUrlConsts;
import com.example.dto.Operation;
import com.example.dto.UserArea;
import com.example.dto.UserInfo;
import com.example.dto.UserRoleInfo;
import com.example.entity.ClientResource;
import com.example.entity.SysUser;
import com.example.mapper.ClientResourceMapper;
import com.example.mapper.SysUserMapper;
import com.example.service.UserService;
import com.example.utils.RestTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by YangGuanRong
 * date: 2023/1/3
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    ClientResourceMapper clientResourceMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("执行loadUserByUsername = " + username);

        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername,username);

        List<SysUser> sysUsers = userMapper.selectList(queryWrapper);

        if (CollectionUtils.isEmpty(sysUsers)){
            throw new UsernameNotFoundException("用户名不存在");
        }
        SysUser user = sysUsers.get(0);
        return user;
    }

    @Override
    public SysUser queryUserByPhone(String phone) {

        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getPhone,phone);

        List<SysUser> sysUsers = userMapper.selectList(queryWrapper);

        if (CollectionUtils.isEmpty(sysUsers)){
            return null;
        }
        return sysUsers.get(0);
    }


    @Override
    public List<UserRoleInfo> getUserRoleInfo(long userId, String clientId) {
        try{
            ClientResource clientResource = getClientResourceByClientId(clientId);
            String url = clientResource.getResourceServerUrl() + ResourceUrlConsts.USER_ROLE_URL;
            HttpHeaders header = RestTemplateUtil.getHeader(new HashMap<>());
            JSONObject param = new JSONObject();
            param.put("userId",userId);
            JSONObject jsonObject = RestTemplateUtil.postWithObject(url, param, header);
            JSONArray data = jsonObject.getJSONArray("data");
            if (data != null){
                return JSON.parseArray(JSON.toJSONString(data),UserRoleInfo.class);
            }
        }catch (Exception e){
            log.error("getUserRoleInfo--查询角色失败：{}",e.getMessage(),e);
        }

        return new ArrayList<>();
    }

    @Override
    public List<Operation> getOperationList(long userId, String clientId) {
        try {
            ClientResource clientResource = getClientResourceByClientId(clientId);
            String url = clientResource.getResourceServerUrl() + ResourceUrlConsts.USER_OPERATION_URL;
            HttpHeaders header = RestTemplateUtil.getHeader(new HashMap<>());
            JSONObject param = new JSONObject();
            param.put("userId",userId);
            JSONObject jsonObject = RestTemplateUtil.postWithObject(url, param, header);
            JSONArray data = jsonObject.getJSONArray("data");
            if (data != null){
                return JSON.parseArray(JSON.toJSONString(data),Operation.class);
            }
        }catch (Exception e){
            log.error("getOperationList--查询接口权限失败：{}",e.getMessage(),e);
        }

        return new ArrayList<>();
    }

    @Override
    public List<TreeNode> getOauthResources(long userId, String clientId) {
        try {
            ClientResource clientResource = getClientResourceByClientId(clientId);
            String url = clientResource.getResourceServerUrl() + ResourceUrlConsts.USER_RESOURCE_URL;
            HttpHeaders header = RestTemplateUtil.getHeader(new HashMap<>());
            JSONObject param = new JSONObject();
            param.put("userId",userId);
            JSONObject jsonObject = RestTemplateUtil.postWithObject(url, param, header);
            JSONArray data = jsonObject.getJSONArray("data");
            if (data != null){
                return JSON.parseArray(JSON.toJSONString(data),TreeNode.class);
            }
        }catch (Exception e){
            log.error("getOperationList--查询菜单权限失败：{}",e.getMessage(),e);
        }

        return new ArrayList<>();
    }

    @Override
    public List<UserArea> getUserAreaList(long userId, String clientId) {
        try{
            ClientResource clientResource = getClientResourceByClientId(clientId);
            String url = clientResource.getResourceServerUrl() + ResourceUrlConsts.USER_AREA_URL;
            HttpHeaders header = RestTemplateUtil.getHeader(new HashMap<>());
            JSONObject param = new JSONObject();
            param.put("userId",userId);
            JSONObject jsonObject = RestTemplateUtil.postWithObject(url, param, header);
            JSONArray data = jsonObject.getJSONArray("data");
            if (data != null){
                return JSON.parseArray(JSON.toJSONString(data),UserArea.class);
            }
        }catch (Exception e){
            log.error("getUserAreaList--查询数据权限失败：{}",e.getMessage(),e);
        }

        return new ArrayList<>();
    }

    @Override
    public UserInfo getUserAuthInfo(long userId, String clientId) {
        try{
            ClientResource clientResource = getClientResourceByClientId(clientId);
            String url = clientResource.getResourceServerUrl() + ResourceUrlConsts.AUTH_INFO_URL;
            HttpHeaders header = RestTemplateUtil.getHeader(new HashMap<>());
            JSONObject param = new JSONObject();
            param.put("userId",userId);
            JSONObject jsonObject = RestTemplateUtil.postWithObject(url, param, header);
            JSONObject data = jsonObject.getJSONObject("data");
            if (data != null){
                return JSON.parseObject(JSON.toJSONString(data),UserInfo.class);
            }
        }catch (Exception e){
            log.error("getUserAuthInfo--查询权限信息失败：{}",e.getMessage(),e);
        }

        return new UserInfo();
    }

    /**
     * 根据clientId获取clientResource
     * @param clientId
     * @return
     */
    private ClientResource getClientResourceByClientId(String clientId){

        LambdaQueryWrapper<ClientResource> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClientResource::getClientId,clientId);

        ClientResource clientResource = clientResourceMapper.selectOne(queryWrapper);
        return clientResource;
    }
}
