package com.king.paddleup.domain.bid.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BidPlacedEvent(
        UUID auctionId,
        UUID bidId,
        UUID bidderId,
        BigDecimal bidAmount,
        Instant createdAt
) {

}
