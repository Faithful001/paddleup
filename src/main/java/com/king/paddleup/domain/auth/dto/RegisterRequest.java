package com.king.paddleup.domain.auth.dto;

public record RegisterRequest(
        String username,
        String email,
        String firstName,
        String lastName,
        String password
) {}
