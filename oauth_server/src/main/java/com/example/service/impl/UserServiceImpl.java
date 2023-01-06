package com.example.service.impl;

import com.example.mapper.UserMapper;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by YangGuanRong
 * date: 2023/1/3
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("执行loadUserByUsername = " + username);

        if (!"admin".equals(username)){
            throw new UsernameNotFoundException("用户名不存在");
        }
        String password = passwordEncoder.encode("123456");
        return new User(username,password,
                // ROLE_abc 角色前缀 ROLE_ 固定
                AuthorityUtils.commaSeparatedStringToAuthorityList("admin,normal"));
//        return userMapper.findByUsername(username);
    }
}
