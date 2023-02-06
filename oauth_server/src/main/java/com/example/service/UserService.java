package com.example.service;

import com.example.entity.SysUser;
import org.springframework.security.core.userdetails.UserDetailsService;



public interface UserService extends UserDetailsService {

    SysUser queryUserByPhone(String phone);

}
