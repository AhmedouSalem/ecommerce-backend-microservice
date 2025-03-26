package com.aryan.gatewayservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JWTUtil {

    @Value("${ecom.token}")
    private String ecomToken;

    public boolean isSystemToken(String token) {
        return token != null && token.equals(ecomToken);
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public Claims getClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {
        // 256-bit key
        return Keys.hmacShaKeyFor("secretsecretsecretsecretsecretsecret".getBytes());
    }
}
