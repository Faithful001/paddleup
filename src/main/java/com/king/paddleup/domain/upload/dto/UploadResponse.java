package com.king.paddleup.domain.upload.dto;

import com.king.paddleup.domain.media.enums.MediaType;

public record UploadResponse(
        String url,
        String publicId,
        MediaType type,
        String format,
        long bytes
) {}
