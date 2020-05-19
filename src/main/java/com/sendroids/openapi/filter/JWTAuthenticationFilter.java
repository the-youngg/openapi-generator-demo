package com.sendroids.openapi.filter;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.sendroids.openapi.util.auth.JWTToken;
import com.sendroids.openapi.util.auth.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Slf4j
public class JWTAuthenticationFilter extends BasicAuthenticationFilter {

    // 可以追加白名单

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException {

        try {

            if (WhiteListAntMatcher.isWhiteList(req)) {
                log.info("WHITE_LIST " + req.getRequestURI());
                try {
                    chain.doFilter(req, res);
                } catch (Exception e) {
                    log.warn("Filter chain to next {} not work", req.getRequestURI());
                    e.printStackTrace();
                }
                return;
            }
            log.info("WHITE_LIST excluded: " + req.getRequestURI());

            // if (!requestURI.startsWith("/api/")) {
            //     chain.doFilter(req, res);
            //     return;
            // }

            String authorizationHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
            // 为了图片能访问S3私有图片，前端启用了session cookie, 让普通的<img src="/api/aws/s3/get/filename.jpg" />也会自动附带jwt token过来
            if (authorizationHeader == null) {
                authorizationHeader = getCookieByName(req);
            }
            if (authorizationHeader == null || !authorizationHeader.startsWith(JWTToken.TOKEN_PREFIX_BEARER)) {
                System.out.println("==========================  No jwt token: " + req.getRequestURI());
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                return;
            }

            String tokenStr = authorizationHeader.substring(JWTToken.TOKEN_PREFIX_BEARER.length()).trim();

            JWTToken decoded = JWTUtil.decodeMayOptional(tokenStr).orElseThrow(
                    () -> new TokenExpiredException("Invalid token."));
            // System.out.println("==========================  [Debug] Request token to spring rest = " + decoded.toString());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            JWTToken.JWT_TOKEN,
                            decoded,
                            Collections.emptyList()
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(req, res);
        } catch (TokenExpiredException e) {
            log.warn("Invalid token: " + req.getHeader(HttpHeaders.AUTHORIZATION) + ". Error:" + e.getMessage());
            // e.printStackTrace();
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
        } catch (Exception e) {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server errors");
            e.printStackTrace();
        }
    }

    private String getCookieByName(HttpServletRequest req) {
        Cookie[] cookie = req.getCookies();

        if (cookie != null) {
            for (Cookie value : cookie) {
                if (value.getName().equals(HttpHeaders.AUTHORIZATION)) {
                    return value.getValue()
                            // 使用了version 0 cookie: https://stackoverflow.com/a/7233959
                            .replaceAll("%20", " ");
                }
            }
        }
        return null;
    }

}
