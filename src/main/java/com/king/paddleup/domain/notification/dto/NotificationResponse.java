package com.king.paddleup.domain.notification.dto;

import com.king.paddleup.domain.notification.NotificationType;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        NotificationType type,
        String title,
        String body,
        UUID entityId,
        boolean isRead,
        Instant createdAt
) {}
