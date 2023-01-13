package com.example.controller;


import com.example.dto.OauthClientDto;
import com.example.dto.ServerResp;
import com.example.entity.ClientResource;
import com.example.entity.OauthClientDetails;
import com.example.service.IClientResourceService;
import com.example.service.IOauthClientDetailsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 客户端连接方式信息表 前端控制器
 * </p>
 *
 * @author dufu
 * @since 2023-01-12
 */
@RestController
@RequestMapping("/clientResource")
@Api("ClientResourceController")
public class ClientResourceController {

    @Autowired
    IClientResourceService clientResourceService;

    @PostMapping(value = "/create")
    @ApiOperation(value = "新增client的resource服务",httpMethod = "POST")
    public ServerResp create(@RequestBody ClientResource clientResource) {

        ClientResource resource = clientResourceService.create(clientResource);

        return new ServerResp(resource);
    }

    @PostMapping(value = "/update")
    @ApiOperation(value = "更新新增client的resource服务",httpMethod = "POST")
    public ServerResp update(@RequestBody ClientResource clientResource) {
        clientResourceService.updateById(clientResource);
        return new ServerResp();
    }

    @PostMapping(value = "/delete")
    @ApiOperation(value = "删除新增client的resource服务",httpMethod = "POST")
    public ServerResp delete(@RequestBody ClientResource clientResource) {
        clientResourceService.delete(clientResource);
        return new ServerResp();
    }
}
