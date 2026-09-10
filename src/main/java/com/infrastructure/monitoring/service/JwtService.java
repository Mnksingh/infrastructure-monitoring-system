package com.infrastructure.monitoring.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final String secret =
            "infrastructure-monitoring-system-secret-key-2026";

    private final long expiration = 1000 * 60 * 60 * 8; // 8 hours for user session
    private final long resetExpiration = 1000 * 60 * 15; // 15 minutes for password reset
    private final long inviteExpiration = 1000 * 60 * 60 * 24 * 3; // 3 days for officer invite

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateToken(String username, String role) {
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .claim("purpose", "AUTH")
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + expiration)
                )
                .signWith(getSigningKey())
                .compact();
    }

    public String generateResetToken(String username) {
        return Jwts.builder()
                .subject(username)
                .claim("purpose", "PASSWORD_RESET")
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + resetExpiration)
                )
                .signWith(getSigningKey())
                .compact();
    }

    public String generateOfficerInviteToken(String username) {
        return Jwts.builder()
                .subject(username)
                .claim("role", "OFFICER")
                .claim("purpose", "OFFICER_INVITE")
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + inviteExpiration)
                )
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String extractRole(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    public String extractPurpose(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("purpose", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenValidForPurpose(String token, String expectedPurpose) {
        try {
            var claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            String purpose = claims.get("purpose", String.class);
            return expectedPurpose.equalsIgnoreCase(purpose);
        } catch (Exception e) {
            return false;
        }
    }
}