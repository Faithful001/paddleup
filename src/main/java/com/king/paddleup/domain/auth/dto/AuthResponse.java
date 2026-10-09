package com.king.paddleup.domain.auth.dto;

import java.util.UUID;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UserDto user
) {
    public AuthResponse(String accessToken, String refreshToken, long expiresIn, UserDto user) {
        this(accessToken, refreshToken, "Bearer", expiresIn, user);
    }

    public record UserDto(
            UUID id,
            String username,
            String email,
            String firstName,
            String lastName,
            Boolean isEmailVerified
    ) {}
}