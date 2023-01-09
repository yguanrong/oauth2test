package com.example.config.oauth;

import com.example.handle.MyAuthenticationFailureHandler;
import com.example.handle.MyAuthenticationSuccessHandler;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;

/**
 * Created by YangGuanRong
 * date: 2023/1/3
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{

    @Autowired
    DataSource dataSource;

    /**
     * 密码的加密方式
     * @return
     */
    @Bean
    public PasswordEncoder myPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 设置认证管理器 便于我们使用 ，使用默认的认证管理器即可
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    /**
     * 使用JDBC数据库方式来保存用户的授权批准记录
     *
     * @return
     */
    @Bean
    public JdbcApprovalStore approvalStore() {
        return new JdbcApprovalStore(dataSource);
    }

    /**
     * 使用JDBC数据库方式来保存授权码
     *
     * @return
     */
    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new JdbcAuthorizationCodeServices(dataSource);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        //表单提交
//        http.formLogin()
//                // 当请求是 /login时 是登录，和表单提交一样
//                .loginProcessingUrl("/login")
//                // 自定义登录页
//                .loginPage("/login.html")
//                // 登录成功之后跳转的页面是POST请求
//                .successForwardUrl("/toMain")
//                // 登录成功处理器不能和 successForwardUrl 共存，有冲突
////                .successHandler(new MyAuthenticationSuccessHandler("http://www.baidu.com"))
//                // 登录失败之后跳转的页面是POST请求
//                .failureForwardUrl("/toFailure");
//                // 登录失败处理器不能和 failureForwardUrl 共存，有冲突
////                .failureHandler(new MyAuthenticationFailureHandler("/error.html"));

        // 授权认证
        http.authorizeRequests()
                // login.html 不需要认证
                .antMatchers("/oauth/**","/login/**","/logout/**").permitAll()
                // 所有请求都需要认证，必须登录之后才能访问
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .permitAll();

        // 关闭csrf防护
        http.csrf().disable();
    }


}