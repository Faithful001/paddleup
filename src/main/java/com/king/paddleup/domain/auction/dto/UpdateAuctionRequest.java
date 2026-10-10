package com.king.paddleup.domain.auction.dto;

import com.king.paddleup.domain.media.dto.MediaItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record UpdateAuctionRequest(
        @NotBlank
        @Size(max = 100)
        String title,

        @Size(max = 500)
        String description,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal startingPrice,

        @DecimalMin(value = "0.01")
        BigDecimal reservePrice,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal minIncrement,

        List<@Valid MediaItem> media,

        @NotNull
        Instant endsAt
) {}