package com.king.paddleup.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateCommentRequest(
        @NotBlank(message = "Comment content cannot be blank")
        @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
        String content,

        UUID parentId
) {}
