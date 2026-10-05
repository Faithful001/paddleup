package com.king.paddleup.domain.bid.dto;

import com.king.paddleup.domain.bid.Bid;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CreateBidResponse(UUID id, UUID auctionId, BigDecimal amount, Instant placedAt) {

    public static CreateBidResponse from(Bid bid) {
        return new CreateBidResponse(bid.getId(), bid.getAuction().getId(), bid.getAmount(), bid.getCreatedAt());
    }
}