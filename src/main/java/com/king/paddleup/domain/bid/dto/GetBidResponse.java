package com.king.paddleup.domain.bid.dto;

import org.hibernate.validator.constraints.UUID;

import java.math.BigDecimal;
import java.time.Instant;

public record GetBidResponse(
        UUID id,
        BigDecimal amount,
        BidderResponse bidder,
        Instant placedAt
) {}