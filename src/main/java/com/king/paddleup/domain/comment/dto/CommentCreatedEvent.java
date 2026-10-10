package com.king.paddleup.domain.comment.dto;

import com.king.paddleup.domain.media.dto.MediaItem;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CommentCreatedEvent(
        UUID auctionId,
        UUID commentId,
        UUID authorId,
        String authorUsername,
        String content,
        List<MediaItem> media,
        UUID parentId,
        boolean isSeller,
        Instant createdAt
) {}
