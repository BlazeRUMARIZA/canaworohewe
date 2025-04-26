package com.canaworohewe.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration

public class JwtConfig {
    @Value("${jwt.secret:default-secret-key-must-be-replaced-in-production}")
    private String secret;
    
    @Value("${jwt.expiration:86400}")
    private Long expiration;

    @Bean
    public SecretKey secretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Long getExpiration() {
        return expiration;
    }
}
