package com.king.paddleup.domain.auction.dto;

import com.king.paddleup.domain.auction.enums.AuctionStatus;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CreateAuctionRequest(
        @NotBlank String title,

        String description,

        @NotNull
        @Positive
        @Digits(integer = 10, fraction = 2)
        BigDecimal startingPrice,

        @Positive
        @Digits(integer = 10, fraction = 2)
        BigDecimal reservePrice,

        @NotNull
        @Positive
        @Digits(integer = 10, fraction = 2)
        BigDecimal minIncrement,

        @NotNull AuctionStatus status,

        List<String> imageUrls,

        Instant endsAt
) {
}
