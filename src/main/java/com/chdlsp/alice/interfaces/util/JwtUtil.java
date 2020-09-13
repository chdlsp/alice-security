package com.chdlsp.alice.interfaces.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;

public class JwtUtil {

    private Key key;

    public JwtUtil(String secret) {
        Key key = Keys.hmacShaKeyFor(secret.getBytes());
        this.key = key;
    }

    public String createToken(Long userId, String userName) {

        return Jwts.builder()
                .claim("userId", userId)
                .claim("userName", userName)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims getClaims(String token) {

        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }
}
