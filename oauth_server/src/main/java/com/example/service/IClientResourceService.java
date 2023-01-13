package com.example.service;

import com.example.entity.ClientResource;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.OauthClientDetails;

/**
 * <p>
 * 客户端连接方式信息表 服务类
 * </p>
 *
 * @author dufu
 * @since 2023-01-12
 */
public interface IClientResourceService extends IService<ClientResource> {

    ClientResource create(ClientResource clientResource);

    void delete(ClientResource clientResource);

}
