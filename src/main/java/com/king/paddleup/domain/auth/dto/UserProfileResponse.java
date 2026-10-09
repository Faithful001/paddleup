package com.king.paddleup.domain.auth.dto;

import java.time.Instant;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String username,
        String firstName,
        String lastName,
        String email,
        Boolean isEmailVerified,
        Instant emailVerifiedAt,
        Boolean isSuspended,
        Instant createdAt,
        Instant updatedAt
) {}
