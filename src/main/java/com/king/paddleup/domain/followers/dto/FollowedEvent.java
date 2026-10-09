package com.king.paddleup.domain.followers.dto;

import java.time.Instant;
import java.util.UUID;

public record FollowedEvent(
        UUID followerId,
        String followerUsername,
        UUID followingId,
        Instant createdAt
) {}
