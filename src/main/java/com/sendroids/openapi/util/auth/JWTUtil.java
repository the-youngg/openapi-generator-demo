package com.sendroids.openapi.util.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
public class JWTUtil {

    static Algorithm algorithm;
    static JWTVerifier verifier;

    static {
        try {
            algorithm = Algorithm.HMAC256("xiaoyang_key");

            verifier = JWT.require(algorithm).build();
        } catch (Exception e) {
            throw new RuntimeException("UnsupportedEncodingException", e);
        }
    }

    // private static final long WILL_EXPIRE_AFTER = 2592000000L; // A Month(ms): 30 * 24 * 60 * 60 * 1000
    private static final long WILL_EXPIRE_AFTER = 31536000000L; // 365 * 24 * 60 * 60 * 1000; // 1 year(ms)
    private static final String JSON = "json";

    public static String
    encode(
            long expiredAfterSeconds,
            JWTToken token) {

        return _encode(expiredAfterSeconds * 1000, token);
    }

    public static String
    encode(
            JWTToken token) {

        return _encode(WILL_EXPIRE_AFTER, token);
    }

    private static String
    _encode(
            long expiredAtMills,
            JWTToken token) {

        return JWT.create().
                withClaim(JSON, token.toJson()).
                withExpiresAt(
                        new Date(System.currentTimeMillis() + expiredAtMills)).
                sign(algorithm);
    }

    public static Optional<JWTToken>
    decodeMayOptional(String tokenMayContainsBearer) {
        if (Strings.isBlank(tokenMayContainsBearer)) {
            return Optional.empty();
        }

        String token = tokenMayContainsBearer.replace("Bearer ", "");
        try {
            DecodedJWT jwt = verifier.verify(token);
            Date expiresAt = jwt.getExpiresAt();
            if (expiresAt.getTime() < System.currentTimeMillis()) {
                log.warn("JWTToken expired at" + expiresAt);
                return Optional.empty();
            }
            String json = jwt.getClaim(JSON).asString();
            if (json == null) {
                log.warn("Never, JWTToken invalid. token=" + token);
                return Optional.empty();
            }
            ObjectMapper mapper = new ObjectMapper();
            return Optional.of(mapper.readValue(json, JWTToken.class));
        } catch (Exception e) {
            log.warn("Never, JWTToken format NG." + e.getMessage() + " token=" + token);
            return Optional.empty();
        }
    }


    public static void main(String[] args) throws JsonProcessingException {
        val jwt = new JWTToken(1L, new ArrayList<>(), "xiaoyang");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(jwt);
        System.out.println(json);

        System.out.println(_encode(31536000000L, jwt));
    }

}

