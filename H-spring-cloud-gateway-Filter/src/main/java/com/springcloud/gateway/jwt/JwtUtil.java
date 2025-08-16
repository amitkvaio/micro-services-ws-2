package com.springcloud.gateway.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

public class JwtUtil {

    // Secret key (in real apps, store securely e.g., in Vault/Env var)
    private static final String SECRET = "mysecretkeymysecretkeymysecretkey123"; // must be at least 32 chars

    private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // Token validity: 1 hour
    private static final long EXPIRATION_TIME = 1000 * 60 * 60;

    // Generate JWT token
    public static String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role) // add custom claim
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate JWT token
 // Validate JWT token
    public static Jws<Claims> validateToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
}

