package com.sendroids.openapi.service.impl;

import com.sendroids.openapi.repository.UserRepository;
import com.sendroids.openapi.util.auth.JWTToken;
import com.sendroids.openapi.util.auth.JWTUtil;
import lombok.val;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static java.util.Collections.emptyList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        val loginUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return new User(loginUser.getUsername(), loginUser.getPassword(), emptyList());
    }

    public Optional<com.sendroids.openapi.domain.User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    public String issueToken(
            final com.sendroids.openapi.domain.User newLoginUser
    ) {
        String token;

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 180);     //设定token过期时间，默认180天
        Date expiryDate = cal.getTime();

        token = JWTUtil.encode(
                JWTToken
                        .builder()
                        .userId(newLoginUser.getId())
                        .userName(newLoginUser.getUsername())
                        .build());

        return "Bearer " + token;

    }

}
