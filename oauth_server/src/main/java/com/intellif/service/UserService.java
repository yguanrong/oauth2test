package com.intellif.service;

import com.intellif.entity.SysUser;
import org.springframework.security.core.userdetails.UserDetailsService;



public interface UserService extends UserDetailsService {

    SysUser queryUserByPhone(String phone);

}
