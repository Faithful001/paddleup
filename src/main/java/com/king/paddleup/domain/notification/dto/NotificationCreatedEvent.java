package com.king.paddleup.domain.notification.dto;

import java.util.UUID;

public record NotificationCreatedEvent(
        UUID recipientId,
        NotificationResponse notification
) {}
