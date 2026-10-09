package com.king.paddleup.domain.comment.dto;

import java.time.Instant;
import java.util.UUID;

public record CommentCreatedEvent(
        UUID auctionId,
        UUID commentId,
        UUID authorId,
        String authorUsername,
        String content,
        UUID parentId,
        boolean isSeller,
        Instant createdAt
) {}
