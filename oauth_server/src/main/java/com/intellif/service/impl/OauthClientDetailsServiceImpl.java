package com.intellif.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intellif.dto.OauthClientDto;
import com.intellif.entity.OauthClientDetails;
import com.intellif.mapper.OauthClientDetailsMapper;
import com.intellif.service.IOauthClientDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * <p>
 * 客户端记录表 服务实现类
 * </p>
 *
 * @author dufu
 * @since 2023-01-13
 */
@Service
@Slf4j
public class OauthClientDetailsServiceImpl extends ServiceImpl<OauthClientDetailsMapper, OauthClientDetails> implements IOauthClientDetailsService {

    @Autowired
    OauthClientDetailsMapper oauthClientDetailsMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public OauthClientDetails create(OauthClientDto oauthClientDto) {
        OauthClientDetails clientDetails = new OauthClientDetails();
        BeanUtils.copyProperties(oauthClientDto,clientDetails);
        clientDetails.setScope("all");

        //生成clientId、clientSecret
        String clientId = UUID.randomUUID().toString().replace("-","").substring(0,8);
        String clientSecret = UUID.randomUUID().toString().replace("-","").substring(0,16);

        clientDetails.setClientId(clientId);
        clientDetails.setClientSecretMw(clientSecret);
        clientDetails.setClientSecret(passwordEncoder.encode(clientSecret));

        oauthClientDetailsMapper.insert(clientDetails);
        log.info("create:插入成功");
        return null;
    }

    @Override
    public void delete(OauthClientDetails oauthClientDto) {
        LambdaQueryWrapper<OauthClientDetails> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(OauthClientDetails::getClientId,oauthClientDto.getClientId());
        oauthClientDetailsMapper.delete(deleteWrapper);
    }


}
