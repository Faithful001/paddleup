package com.king.paddleup.domain.media.dto;

import com.king.paddleup.domain.media.enums.MediaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MediaItem(
        @NotBlank(message = "Media URL cannot be blank")
        String url,

        @NotNull(message = "Media type must be specified (IMAGE or VIDEO)")
        MediaType type
) {}
