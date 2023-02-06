package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.ClientResource;
import com.example.mapper.ClientResourceMapper;
import com.example.service.IClientResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
public class ClientResourceServiceImpl extends ServiceImpl<ClientResourceMapper, ClientResource> implements IClientResourceService {

    @Autowired
    ClientResourceMapper clientResourceMapper;
    @Override
    public ClientResource create(ClientResource clientResource) {
        int insert = clientResourceMapper.insert(clientResource);
        log.info("插入成功");
        return clientResource;
    }

    @Override
    public void delete(ClientResource clientResource) {
        LambdaQueryWrapper<ClientResource> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(ClientResource::getClientId,clientResource.getClientId());
        clientResourceMapper.delete(deleteWrapper);
    }

}
