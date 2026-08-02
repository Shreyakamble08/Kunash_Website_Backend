package com.kunash.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        String token = Jwts.builder()
                .setSubject(username)           // ✅ Use setSubject() not subject()
                .setIssuedAt(now)               // ✅ Use setIssuedAt()
                .setExpiration(expiryDate)      // ✅ Use setExpiration()
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();

        log.debug("Generated JWT for user: {}", username);
        return token;
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()    // ✅ Use parserBuilder()
                .setSigningKey(getSigningKey())  // ✅ Use setSigningKey()
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()                 // ✅ Use parserBuilder()
                    .setSigningKey(getSigningKey())   // ✅ Use setSigningKey()
                    .build()
                    .parseClaimsJws(token);

            log.debug("JWT token validated successfully");
            return true;
        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }
}