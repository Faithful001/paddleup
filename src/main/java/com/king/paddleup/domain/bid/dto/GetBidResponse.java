package com.king.paddleup.domain.bid.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record GetBidResponse(
        UUID id,
        BigDecimal amount,
        BidderResponse bidder,
        Instant placedAt
) {}