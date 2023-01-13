package com.example.controller;


import com.example.dto.OauthClientDto;
import com.example.dto.ServerResp;
import com.example.entity.OauthClientDetails;
import com.example.service.IOauthClientDetailsService;
import io.jsonwebtoken.Jwts;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 客户端记录表 前端控制器
 * </p>
 *
 * @author dufu
 * @since 2023-01-13
 */
@RestController
@RequestMapping("/oauthClient")
@Api("OauthClientDetailsController")
public class OauthClientDetailsController {

    @Autowired
    IOauthClientDetailsService oauthClientDetailsService;

    @PostMapping(value = "/create")
    @ApiOperation(value = "申请client",httpMethod = "POST")
    public ServerResp create(@RequestBody OauthClientDto oauthClientDto) {

        OauthClientDetails oauthClientDetails = oauthClientDetailsService.create(oauthClientDto);

        return new ServerResp(oauthClientDetails);
    }

    @PostMapping(value = "/update")
    @ApiOperation(value = "更新client",httpMethod = "POST")
    public ServerResp update(@RequestBody OauthClientDetails oauthClientDto) {
        oauthClientDetailsService.updateById(oauthClientDto);
        return new ServerResp();
    }

    @PostMapping(value = "/delete")
    @ApiOperation(value = "删除client",httpMethod = "POST")
    public ServerResp delete(@RequestBody OauthClientDetails oauthClientDto) {
        oauthClientDetailsService.delete(oauthClientDto);
        return new ServerResp();
    }

}
