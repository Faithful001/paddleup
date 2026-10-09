package com.king.paddleup.domain.followers.dto;

import java.time.Instant;
import java.util.UUID;

public record FollowUserResponse(
        UUID id,
        String username,
        String firstName,
        String lastName,
        Instant followedAt
) {
}
