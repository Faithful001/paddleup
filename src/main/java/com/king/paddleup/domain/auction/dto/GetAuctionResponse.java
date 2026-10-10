package com.king.paddleup.domain.auction.dto;

import com.king.paddleup.domain.auction.enums.AuctionStatus;
import com.king.paddleup.domain.media.dto.MediaItem;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GetAuctionResponse(
        UUID id,
        String title,
        String description,
        List<MediaItem> media,
        AuctionStatus status,
        BigDecimal startingPrice,
        BigDecimal minIncrement,
        Instant endsAt,
        SellerResponse seller
) {
    public record SellerResponse(UUID id, String username) {}
}
