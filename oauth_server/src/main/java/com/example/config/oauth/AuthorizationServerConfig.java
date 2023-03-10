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
     * ??????????????????TokenGranter
     */
    private List<TokenGranter> getDefaultTokenGranters(AuthorizationServerEndpointsConfigurer endpoints) {

        ClientDetailsService clientDetails = endpoints.getClientDetailsService();
        AuthorizationServerTokenServices tokenServices = endpoints.getTokenServices();
        AuthorizationCodeServices authorizationCodeServices = endpoints.getAuthorizationCodeServices();
        OAuth2RequestFactory requestFactory = endpoints.getOAuth2RequestFactory();

        List<TokenGranter> tokenGranters = new ArrayList<>();
        // ???????????????
        tokenGranters.add(new AuthorizationCodeTokenGranter(tokenServices, authorizationCodeServices, clientDetails,
                requestFactory));
        // ??????token??????
        tokenGranters.add(new RefreshTokenGranter(tokenServices, clientDetails, requestFactory));
        ImplicitTokenGranter implicit = new ImplicitTokenGranter(tokenServices, clientDetails, requestFactory);
        //
        tokenGranters.add(implicit);
        // ???????????????
        tokenGranters.add(new ClientCredentialsTokenGranter(tokenServices, clientDetails, requestFactory));

        if (authenticationManager != null) {
            // ????????????
            tokenGranters.add(new ResourceOwnerPasswordTokenGranter(authenticationManager, tokenServices,
                    clientDetails, requestFactory));
            // ???????????????
            tokenGranters.add(new SmsCodeGranter(authenticationManager, tokenServices,
                    clientDetails, requestFactory,userService));
        }
        return tokenGranters;
    }

    /**
     * ????????????????????????
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {


        endpoints.authenticationManager(authenticationManager)
                // ??????????????????
                .userDetailsService(userService)
                // ??????jwt????????????store
                .tokenStore(redisTokenStore)
                // ???????????????????????????????????????
                .approvalStore(approvalStore)
                // ????????????????????????
                .authorizationCodeServices(authorizationCodeServices)
                // token?????????????????????token????????????
                .tokenEnhancer(customTokenEnhancer);

        // ??????????????????TokenGranter??????????????????CompositeTokenGranter
        List<TokenGranter> tokenGranters = getDefaultTokenGranters(endpoints);

        // ????????????????????????
        endpoints.tokenGranter(new CompositeTokenGranter(tokenGranters));

        // ????????????
        endpoints.exceptionTranslator(getoAuth2ExceptionWebResponseExceptionTranslator());

    }

    private WebResponseExceptionTranslator<OAuth2Exception> getoAuth2ExceptionWebResponseExceptionTranslator() {
        return e -> {
            log.error("??????????????????", e);
            ServerResp object = new ServerResp();
            if (e instanceof InvalidGrantException || e instanceof InternalAuthenticationServiceException) {
                object.setRespCode(ServerResp.LOGIN_ERROR_CODE);
                object.setRespMessage("????????????????????????");
                object.setRespRemark(e.getMessage());

            } else if (e instanceof InvalidRequestException) {
                object.setRespCode(ServerResp.ERROR_CODE);
                object.setRespMessage("????????????????????????");
                object.setRespRemark(e.getMessage());
            } else if (e instanceof InvalidTokenException) {
                object.setRespCode(ServerResp.LOGIN_ERROR_CODE);
                object.setRespMessage("??????????????????token??????");
                object.setRespRemark(e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else if (e instanceof BadClientCredentialsException || e instanceof UnauthorizedClientException) {
                object.setRespCode(ServerResp.LOGIN_ERROR_CODE);
                object.setRespMessage("?????????????????????");
                object.setRespRemark(e.getMessage());
            } else {
                object.setRespCode(ServerResp.ERROR_CODE);
                object.setRespMessage("????????????");
                object.setRespRemark(e.getMessage());
            }

            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(JSONObject.toJSONString(object));
            return null;
        };
    }

    /**
     * ?????????????????????
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//        /**
//         * ????????????
//         */
//        clients.inMemory()
//                // ??????clientId
//                .withClient("admin")
//                // ????????????clientSecret
//                .secret(passwordEncoder.encode("112233"))
//                // ??????????????????????????????
//                .accessTokenValiditySeconds(3600)
//                // ???????????????????????????
//                .refreshTokenValiditySeconds(864000)
//                // ????????????
//                .autoApprove(true)
//                // ?????????????????????
//                .redirectUris("http://localhost:9002/login")
//                // ????????????
//                .scopes("all")
//                // ?????? grant_type ?????????????????????
//                .authorizedGrantTypes("password","refresh_token","authorization_code");

        /**
         * ???????????????????????????????????????oauth_client_details
         * ???????????????????????????ClientID
         */
        clients.jdbc(dataSource);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {

        // ????????????????????????????????????????????????????????????????????????
        security.tokenKeyAccess("isAuthenticated()");
    }
}