package com.king.paddleup.domain.comment.dto;

import com.king.paddleup.domain.media.dto.MediaItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record CreateCommentRequest(
        @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
        String content,

        UUID parentId,

        List<@Valid MediaItem> media
) {}
