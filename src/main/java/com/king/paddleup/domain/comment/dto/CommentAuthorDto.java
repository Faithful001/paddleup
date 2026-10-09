package com.king.paddleup.domain.comment.dto;

import java.util.UUID;

public record CommentAuthorDto(
        UUID id,
        String username,
        boolean isSeller,
        boolean isBidder
) {}
