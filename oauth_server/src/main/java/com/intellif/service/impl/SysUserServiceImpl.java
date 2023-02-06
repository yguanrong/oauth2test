package com.intellif.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intellif.entity.SysUser;
import com.intellif.mapper.SysUserMapper;
import com.intellif.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dufu
 * @since 2023-01-07
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Autowired
    SysUserMapper sysUserMapper;

    @Override
    public SysUser create(SysUser sysUser) {
        sysUserMapper.insert(sysUser);
        return sysUser;
    }

    @Override
    public void delete(SysUser sysUser) {
        sysUserMapper.deleteById(sysUser.getId());
    }
}
