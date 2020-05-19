package com.sendroids.openapi.config;


import com.sendroids.openapi.filter.JWTAuthenticationFilter;
import com.sendroids.openapi.filter.JWTLoginFilter;
import com.sendroids.openapi.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;


/**
 * SpringSecurity的配置
 * 将JWTLoginFilter,JWTAuthenticationFilter组合在一起
 */
@EnableGlobalMethodSecurity(securedEnabled = true)
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    //url入口白名单
    public static final String[] AUTH_WHITELIST = {
            "/login", //Spring security默认登录
            "/user/auth/**", //自定义登录
            "/user/addUser", //注册用户

            //  openAPI文档
            "/swagger-resources/**",
            "/v2/**",
            "/webjars/**",
            "/swagger-ui.html/**",
            "/api-docs/**",
            "/doc.html",
    };

    public static final List<String> WHITE_LIST = Arrays.asList(AUTH_WHITELIST);

    private UserDetailsServiceImpl userDetailsService;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest().authenticated()  // 所有请求需要身份认证
                .and()

                .addFilter(new JWTLoginFilter(authenticationManager(), userDetailsService))
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                .logout() // 默认注销行为为logout，可以通过下面的方式来修改
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .permitAll();// 设置注销成功后跳转页面，默认是跳转到登录页面;
    }

    @Autowired
    public void configureAuth(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder());
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
