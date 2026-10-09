package com.king.paddleup.domain.comment.dto;

import java.time.Instant;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        UUID auctionId,
        CommentAuthorDto author,
        String content,
        UUID parentId,
        long replyCount,
        boolean isDeleted,
        Instant createdAt
) {}
