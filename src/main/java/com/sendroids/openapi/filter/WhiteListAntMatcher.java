package com.sendroids.openapi.filter;


import com.sendroids.openapi.config.WebSecurityConfig;
import lombok.Synchronized;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.AbstractRequestMatcherRegistry;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * How to  use:
 * 1) change WHITE_LIST define in line #26
 * 2) In your Application JWT Filter:
 *
 *     protected void doFilterInternal(HttpServletRequest req,
 *                                     HttpServletResponse res,
 *                                     FilterChain chain) throws IOException, ServletException {
 *         // Add this block codes
 *         if (WhiteListAntMatcher.isWhiteList(req)) {
 *             chain.doFilter(req, res);
 *             return;
 *         }
 */
public class WhiteListAntMatcher extends AbstractRequestMatcherRegistry<List<RequestMatcher>> {

    private static List<RequestMatcher> requestMatchers;

    static {
        initMatcher();
    }

    @Synchronized
    private static void initMatcher() {
        if (requestMatchers == null) {
            requestMatchers = new WhiteListAntMatcher()
                .antMatchers(WebSecurityConfig.WHITE_LIST.toArray(new String[]{}));
        }
    }

    private WhiteListAntMatcher() {
    }

    public static boolean isWhiteList(HttpServletRequest req) {
        initMatcher();

        for (RequestMatcher matcher : requestMatchers) {
            boolean isWhiteList = matcher.matches(req);
            if (isWhiteList) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<RequestMatcher> mvcMatchers(String... strings) {
        throw new RuntimeException("Not support");
    }

    @Override
    public List<RequestMatcher> mvcMatchers(HttpMethod httpMethod, String... strings) {
        throw new RuntimeException("Not support");
    }

    @Override
    protected List<RequestMatcher> chainRequestMatchers(List<RequestMatcher> list) {
        return list;
    }
}