package com.sendroids.openapi.util.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

@ToString
@Data
@Builder
public class JWTToken {

    long userId;
    List<String> roles;
    String userName;

    public static final String JWT_TOKEN = "JWT_TOKEN";
    public static final String TOKEN_PREFIX_BEARER = "Bearer ";

    public static JWTToken EMPTY = new JWTToken(
            Integer.MIN_VALUE, Collections.emptyList(), "userName");

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        String str = "";
        try {
            str = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return str;
    }

}
