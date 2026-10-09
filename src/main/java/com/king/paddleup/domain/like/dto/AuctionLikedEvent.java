package com.king.paddleup.domain.like.dto;

import java.time.Instant;
import java.util.UUID;

public record AuctionLikedEvent(
        UUID auctionId,
        String auctionTitle,
        UUID likerId,
        String likerUsername,
        UUID sellerId,
        Instant createdAt
) {}
