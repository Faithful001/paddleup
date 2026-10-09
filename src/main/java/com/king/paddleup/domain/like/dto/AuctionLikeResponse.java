package com.king.paddleup.domain.like.dto;

import java.time.Instant;
import java.util.UUID;

public record AuctionLikeResponse(
        UUID id,
        UUID auctionId,
        UUID userId,
        Instant createdAt,
        long totalLikes
) {}
