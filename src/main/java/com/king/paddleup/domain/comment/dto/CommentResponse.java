package com.king.paddleup.domain.comment.dto;

import com.king.paddleup.domain.media.dto.MediaItem;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        UUID auctionId,
        CommentAuthorDto author,
        String content,
        List<MediaItem> media,
        UUID parentId,
        long replyCount,
        boolean isDeleted,
        Instant createdAt
) {}
