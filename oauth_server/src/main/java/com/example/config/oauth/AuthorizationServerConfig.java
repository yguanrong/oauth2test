package com.example.config.oauth;


import com.alibaba.fastjson2.JSONObject;
import com.example.dto.ServerResp;
import com.example.granter.SmsCodeGranter;
import com.example.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.*;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YangGuanRong
 * date: 2023/1/3
 */
@Configuration
@EnableAuthorizationServer
@Slf4j
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    @Qualifier("redisTokenStore")
    TokenStore redisTokenStore;

    @Autowired
    CustomTokenEnhancer customTokenEnhancer;

    @Autowired
    private DataSource dataSource;

    @Autowired
    JdbcApprovalStore approvalStore;

    @Autowired
    AuthorizationCodeServices authorizationCodeServices;

    @Resource
    private HttpServletResponse response;

    /**
     * 初始化所有的TokenGranter
     */
    private List<TokenGranter> getDefaultTokenGranters(AuthorizationServerEndpointsConfigurer endpoints) {

        ClientDetailsService clientDetails = endpoints.getClientDetailsService();
        AuthorizationServerTokenServices tokenServices = endpoints.getTokenServices();
        AuthorizationCodeServices authorizationCodeServices = endpoints.getAuthorizationCodeServices();
        OAuth2RequestFactory requestFactory = endpoints.getOAuth2RequestFactory();

        List<TokenGranter> tokenGranters = new ArrayList<>();
        // 授权码模式
        tokenGranters.add(new AuthorizationCodeTokenGranter(tokenServices, authorizationCodeServices, clientDetails,
                requestFactory));
        // 刷新token模式
        tokenGranters.add(new RefreshTokenGranter(tokenServices, clientDetails, requestFactory));
        ImplicitTokenGranter implicit = new ImplicitTokenGranter(tokenServices, clientDetails, requestFactory);
        //
        tokenGranters.add(implicit);
        // 客户端模式
        tokenGranters.add(new ClientCredentialsTokenGranter(tokenServices, clientDetails, requestFactory));

        if (authenticationManager != null) {
            // 密码模式
            tokenGranters.add(new ResourceOwnerPasswordTokenGranter(authenticationManager, tokenServices,
                    clientDetails, requestFactory));
            // 短信验证码
            tokenGranters.add(new SmsCodeGranter(authenticationManager, tokenServices,
                    clientDetails, requestFactory,userService));
        }
        return tokenGranters;
    }

    /**
     * 密码模式需要配置
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {


        endpoints.authenticationManager(authenticationManager)
                // 自定义用户表
                .userDetailsService(userService)
                // 配置jwt存储令牌store
                .tokenStore(redisTokenStore)
                // 使用保存用户的授权批准记录
                .approvalStore(approvalStore)
                // 保存授权码的方式
                .authorizationCodeServices(authorizationCodeServices)
                // token增强器，自定义token返回内容
                .tokenEnhancer(customTokenEnhancer);

        // 初始化所有的TokenGranter，并且类型为CompositeTokenGranter
        List<TokenGranter> tokenGranters = getDefaultTokenGranters(endpoints);

        // 自定义令牌授予者
        endpoints.tokenGranter(new CompositeTokenGranter(tokenGranters));

        // 异常处理
        endpoints.exceptionTranslator(getoAuth2ExceptionWebResponseExceptionTranslator());

    }

    private WebResponseExceptionTranslator<OAuth2Exception> getoAuth2ExceptionWebResponseExceptionTranslator() {
        return e -> {
            log.error("权限校验异常", e);
            ServerResp object = new ServerResp();
            if (e instanceof InvalidGrantException || e instanceof InternalAuthenticationServiceException) {
                object.setRespCode(ServerResp.LOGIN_ERROR_CODE);
                object.setRespMessage("用户名或密码错误");
                object.setRespRemark(e.getMessage());

            } else if (e instanceof InvalidRequestException) {
                object.setRespCode(ServerResp.ERROR_CODE);
                object.setRespMessage("不支持的请求类型");
                object.setRespRemark(e.getMessage());
            } else if (e instanceof InvalidTokenException) {
                object.setRespCode(ServerResp.LOGIN_ERROR_CODE);
                object.setRespMessage("没有登录或者token错误");
                object.setRespRemark(e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else if (e instanceof BadClientCredentialsException || e instanceof UnauthorizedClientException) {
                object.setRespCode(ServerResp.LOGIN_ERROR_CODE);
                object.setRespMessage("权限验证不通过");
                object.setRespRemark(e.getMessage());
            } else {
                object.setRespCode(ServerResp.ERROR_CODE);
                object.setRespMessage("未知错误");
                object.setRespRemark(e.getMessage());
            }

            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(JSONObject.toJSONString(object));
            return null;
        };
    }

    /**
     * 配置客户端详情
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//        /**
//         * 内存方式
//         */
//        clients.inMemory()
//                // 配置clientId
//                .withClient("admin")
//                // 配置配置clientSecret
//                .secret(passwordEncoder.encode("112233"))
//                // 配置访问令牌的有效期
//                .accessTokenValiditySeconds(3600)
//                // 配置刷新令牌有效期
//                .refreshTokenValiditySeconds(864000)
//                // 自动授权
//                .autoApprove(true)
//                // 授权成功后跳转
//                .redirectUris("http://localhost:9002/login")
//                // 权限范围
//                .scopes("all")
//                // 配置 grant_type 标识授权码模式
//                .authorizedGrantTypes("password","refresh_token","authorization_code");

        /**
         * 数据库读取，默认使用的表是oauth_client_details
         * 允许客户端自己申请ClientID
         */
        clients.jdbc(dataSource);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {

        // 获取秘钥是否需要身份认证，使用单点登录时必须配置
        security.tokenKeyAccess("isAuthenticated()");
    }
}