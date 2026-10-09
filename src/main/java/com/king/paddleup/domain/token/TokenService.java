package com.king.paddleup.domain.token;

import com.king.paddleup.domain.token.enums.TokenType;
import com.king.paddleup.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final TokenRepository tokenRepository;
    private final StringRedisTemplate redisTemplate;

    private static final String TOKEN_KEY_PREFIX = "token:";
    private static final String BLACKLIST_KEY_PREFIX = "blacklist:";
    private static final String USER_TOKENS_PREFIX = "user_tokens:";

    @Transactional
    public Token saveToken(User user, String tokenString, TokenType tokenType, Instant expiresAt) {
        Token token = Token.builder()
                .user(user)
                .token(tokenString)
                .tokenType(tokenType)
                .revoked(false)
                .expired(false)
                .expiresAt(expiresAt)
                .build();

        Token saved = tokenRepository.save(token);

        cacheTokenInRedis(tokenString, user.getId(), tokenType, expiresAt);
        return saved;
    }

    public boolean isTokenValid(String tokenString, TokenType expectedType) {
        try {
            Boolean isBlacklisted = redisTemplate.hasKey(BLACKLIST_KEY_PREFIX + tokenString);
            if (Boolean.TRUE.equals(isBlacklisted)) {
                return false;
            }

            String cachedValue = redisTemplate.opsForValue().get(TOKEN_KEY_PREFIX + tokenString);
            if (cachedValue != null) {
                return expectedType == null || cachedValue.endsWith(":" + expectedType.name());
            }

            // Fallback to database check
            Optional<Token> tokenOpt = tokenRepository.findByToken(tokenString);
            if (tokenOpt.isPresent()) {
                Token token = tokenOpt.get();
                boolean valid = !token.isRevoked()
                        && !token.isExpired()
                        && token.getExpiresAt().isAfter(Instant.now())
                        && (expectedType == null || token.getTokenType() == expectedType);

                if (valid) {
                    cacheTokenInRedis(tokenString, token.getUser().getId(), token.getTokenType(), token.getExpiresAt());
                    return true;
                }
            }
        } catch (Exception e) {
            log.warn("Redis check failed, falling back to DB for token validation: {}", e.getMessage());
            Optional<Token> tokenOpt = tokenRepository.findByToken(tokenString);
            return tokenOpt.map(t -> !t.isRevoked()
                    && !t.isExpired()
                    && t.getExpiresAt().isAfter(Instant.now())
                    && (expectedType == null || t.getTokenType() == expectedType)
            ).orElse(false);
        }

        return false;
    }

    @Transactional
    public void revokeToken(String tokenString) {
        tokenRepository.findByToken(tokenString).ifPresent(token -> {
            token.setRevoked(true);
            token.setExpired(true);
            tokenRepository.save(token);
        });

        try {
            redisTemplate.delete(TOKEN_KEY_PREFIX + tokenString);
            redisTemplate.opsForValue().set(BLACKLIST_KEY_PREFIX + tokenString, "revoked", Duration.ofDays(7));
        } catch (Exception e) {
            log.warn("Failed to invalidate token in Redis: {}", e.getMessage());
        }
    }

    @Transactional
    public void revokeAllUserTokens(UUID userId) {
        tokenRepository.revokeAllUserTokens(userId);

        try {
            Set<String> tokens = redisTemplate.opsForSet().members(USER_TOKENS_PREFIX + userId);
            if (tokens != null && !tokens.isEmpty()) {
                for (String t : tokens) {
                    redisTemplate.delete(TOKEN_KEY_PREFIX + t);
                    redisTemplate.opsForValue().set(BLACKLIST_KEY_PREFIX + t, "revoked", Duration.ofDays(7));
                }
            }
            redisTemplate.delete(USER_TOKENS_PREFIX + userId);
        } catch (Exception e) {
            log.warn("Failed to invalidate all user tokens in Redis: {}", e.getMessage());
        }
    }

    @Transactional
    public void revokeAllUserTokensByType(UUID userId, TokenType tokenType) {
        tokenRepository.revokeAllUserTokensByType(userId, tokenType);
    }

    private void cacheTokenInRedis(String tokenString, UUID userId, TokenType tokenType, Instant expiresAt) {
        try {
            Duration ttl = Duration.between(Instant.now(), expiresAt);
            if (!ttl.isNegative() && !ttl.isZero()) {
                redisTemplate.opsForValue().set(
                        TOKEN_KEY_PREFIX + tokenString,
                        userId + ":" + tokenType.name(),
                        ttl
                );
                redisTemplate.opsForSet().add(USER_TOKENS_PREFIX + userId, tokenString);
            }
        } catch (Exception e) {
            log.warn("Failed to cache token in Redis: {}", e.getMessage());
        }
    }
}
