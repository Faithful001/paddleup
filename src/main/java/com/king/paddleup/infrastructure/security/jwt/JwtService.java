package com.king.paddleup.infrastructure.security.jwt;

import com.king.paddleup.domain.token.enums.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    public String generateAccessToken(UUID userId, String email) {
        return buildToken(userId, email, TokenType.ACCESS, jwtProperties.getAccessTokenExpirationMs());
    }

    public String generateRefreshToken(UUID userId, String email) {
        return buildToken(userId, email, TokenType.REFRESH, jwtProperties.getRefreshTokenExpirationMs());
    }

    public String generateEmailVerificationToken(UUID userId, String email) {
        return buildToken(userId, email, TokenType.EMAIL_VERIFICATION, jwtProperties.getEmailVerificationTokenExpirationMs());
    }

    public String generatePasswordResetToken(UUID userId, String email) {
        return buildToken(userId, email, TokenType.PASSWORD_RESET, jwtProperties.getPasswordResetTokenExpirationMs());
    }

    public String generateToken(UUID userId, String email) {
        return generateAccessToken(userId, email);
    }

    private String buildToken(UUID userId, String email, TokenType type, long expirationMs) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .claim("type", type)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(parseClaims(token).getSubject());
    }

    public String extractEmail(String token) {
        return parseClaims(token).get("email", String.class);
    }

    public String extractTokenType(String token) {
        return parseClaims(token).get("type", String.class);
    }

    public Date extractExpiration(String token) {
        return parseClaims(token).getExpiration();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}