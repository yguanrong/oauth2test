package com.intellif.service;

import com.intellif.dto.OauthClientDto;
import com.intellif.entity.OauthClientDetails;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 客户端记录表 服务类
 * </p>
 *
 * @author dufu
 * @since 2023-01-13
 */
public interface IOauthClientDetailsService extends IService<OauthClientDetails> {

    /**
     * 生成clientId
     * @param oauthClientDto
     * @return
     */
    OauthClientDetails create(OauthClientDto oauthClientDto);

    /**
     * 删除
     * @param oauthClientDto
     */
    void delete(OauthClientDetails oauthClientDto);

}
