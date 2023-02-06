package com.intellif.granter;

import com.intellif.entity.SysUser;
import com.intellif.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * 自定义短信验证码授权登录
 *
 * @Date: 2022/03/23/21:05
 * @Description: SmsCodeGranter
 */
@Slf4j
public class SmsCodeGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "sms_code";

    protected final AuthenticationManager authenticationManager;

    private UserService userService;

    public SmsCodeGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices,
                          ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory,
                          UserService userService) {
        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {

        Map<String, String> parameters = new LinkedHashMap(tokenRequest.getRequestParameters());
        String telephone = parameters.get("telePhone");
        String code = parameters.get("code");
        //根据传入的手机号从redis中拿code，比较code是否相等
        if(!code.equals("123123")){
            throw new InvalidGrantException("短信验证码填写错误.");
        }
        // 根据手机号码查询用户信息
        SysUser user = userService.queryUserByPhone(telephone);
        if (user == null) {
            throw new InvalidGrantException("手机号码填写错误.");
        }

        AbstractAuthenticationToken userAuth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        OAuth2Request storedOAuth2Request = this.getRequestFactory().createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(storedOAuth2Request, userAuth);


    }
}
