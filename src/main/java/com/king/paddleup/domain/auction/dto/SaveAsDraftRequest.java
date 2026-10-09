package com.king.paddleup.domain.auction.dto;

import com.king.paddleup.domain.auction.enums.AuctionStatus;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record SaveAsDraftRequest(
        String title,

        String description,

        @Positive
        @Digits(integer = 10, fraction = 2)
        BigDecimal startingPrice,

        @Positive
        @Digits(integer = 10, fraction = 2)
        BigDecimal reservePrice,

        @Positive
        @Digits(integer = 10, fraction = 2)
        BigDecimal minIncrement,

        @NotNull AuctionStatus status,

        List<String> imageUrls,

        Instant endsAt
) {
}
