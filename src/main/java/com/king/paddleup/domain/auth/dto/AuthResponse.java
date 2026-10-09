package com.king.paddleup.domain.auth.dto;

public record AuthResponse(
        String accessToken,
        String tokenType
) {}