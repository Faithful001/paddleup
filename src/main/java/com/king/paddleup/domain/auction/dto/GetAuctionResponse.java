package com.king.paddleup.domain.auction.dto;

import com.king.paddleup.domain.auction.Auction;
import com.king.paddleup.domain.auction.enums.AuctionStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GetAuctionResponse(
        UUID id,
        String title,
        String description,
        List<String> imageUrls,
        AuctionStatus status,
        BigDecimal startingPrice,
        BigDecimal minIncrement,
        Instant endsAt,
        SellerResponse seller
) {
    public record SellerResponse(UUID id, String username) {}
}
