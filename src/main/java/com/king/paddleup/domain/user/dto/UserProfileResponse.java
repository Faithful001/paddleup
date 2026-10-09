package com.king.paddleup.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserProfileResponse(
        UUID id,
        String username,
        String firstName,
        String lastName,
        String email,
        Boolean isEmailVerified,
        Instant createdAt,
        long followersCount,
        long followingCount,
        long auctionsCount,
        Boolean isFollowing
) {
}
