package com.example.service.impl;

import com.example.entity.ClientResource;
import com.example.mapper.ClientResourceMapper;
import com.example.service.IClientResourceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 客户端连接方式信息表 服务实现类
 * </p>
 *
 * @author dufu
 * @since 2023-01-12
 */
@Service
public class ClientResourceServiceImpl extends ServiceImpl<ClientResourceMapper, ClientResource> implements IClientResourceService {

}
