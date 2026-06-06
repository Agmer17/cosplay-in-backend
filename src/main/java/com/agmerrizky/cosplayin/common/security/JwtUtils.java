package com.agmerrizky.cosplayin.common.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

    private final SecretKey secret;
    private final long expirationTime = 604800000;

    public JwtUtils(@Value("${JWT_SECRET}") String key) {
        this.secret = Keys.hmacShaKeyFor(
                key.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UUID id, String role) {
        return Jwts.builder()
                .subject(id.toString())
                .claim("role", role.toUpperCase())
                .claim("id", id)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(this.secret)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(this.secret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
