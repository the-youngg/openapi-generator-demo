package com.sendroids.openapi.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sendroids.openapi.domain.User;
import com.sendroids.openapi.service.impl.UserDetailsServiceImpl;
import com.sendroids.openapi.util.auth.JWTToken;
import com.sendroids.openapi.util.auth.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 验证用户名密码正确后，生成一个token，并将token返回给客户端
 *
 * @method attemptAuthentication ：接收并解析用户凭证。
 * @method successfulAuthentication ：用户成功登录后，这个方法会被调用，我们在这个方法里生成token。
 */
@Slf4j
public class JWTLoginFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private UserDetailsServiceImpl userDetailsService;
    private User loginUser;

    public JWTLoginFilter(AuthenticationManager authenticationManager,
                          UserDetailsServiceImpl userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    // 接收并解析用户凭证
    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest req,
            HttpServletResponse res
    ) throws AuthenticationException, NullPointerException {
        try {
            loginUser = new ObjectMapper().readValue(req.getInputStream(), User.class);
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginUser.getUsername(),
                            loginUser.getPassword(),
                            new ArrayList<>())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 用户成功登录后，这个方法会被调用，在这个方法里生成token
     *
     * @param request  request
     * @param response response
     * @param chain    token链
     * @param auth     认证
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication auth) {

        try {
            //登录成功后更新用户表的token
            String username = loginUser.getUsername();
            val newLoginUser = userDetailsService.getUserByUsername(username).orElseThrow(
                    () -> new IllegalAccessException("token无效")
            );


            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 180);     //设定token过期时间，默认180天
            Date expiryDate = cal.getTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String token = userDetailsService.issueToken(newLoginUser);
            log.info("用户id={}的Jwt Token过期时间为：{}", newLoginUser.getId(), dateFormat.format(expiryDate));


            //把token放到header里面返回给前端
            response.addHeader("Authorization", token);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
