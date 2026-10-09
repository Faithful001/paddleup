package com.king.paddleup.infrastructure.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970337336763979244226452948404D6351";
    private long expirationMs = 900000; // 15 minutes fallback
    private long accessTokenExpirationMs = 900000; // 15 minutes default
    private long refreshTokenExpirationMs = 604800000; // 7 days default
    private long emailVerificationTokenExpirationMs = 86400000; // 24 hours
    private long passwordResetTokenExpirationMs = 3600000; // 1 hour
}
