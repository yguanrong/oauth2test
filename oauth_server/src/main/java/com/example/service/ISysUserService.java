package com.example.service;

import com.example.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dufu
 * @since 2023-01-07
 */
public interface ISysUserService extends IService<SysUser> {

    SysUser create(SysUser sysUser);

    void delete(SysUser sysUser);

}
