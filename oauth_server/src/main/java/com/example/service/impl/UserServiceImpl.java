package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.entity.SysUser;
import com.example.mapper.SysUserMapper;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by YangGuanRong
 * date: 2023/1/3
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private SysUserMapper userMapper;

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
        user.setGrantedAuthorities(AuthorityUtils.commaSeparatedStringToAuthorityList("admin,normal"));

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
}
