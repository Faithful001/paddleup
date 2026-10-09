package com.king.paddleup.domain.notification;

import com.king.paddleup.domain.notification.dto.NotificationResponse;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getBody(),
                notification.getEntityId(),
                notification.getIsRead(),
                notification.getCreatedAt()
        );
    }
}
