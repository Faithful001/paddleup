package com.king.paddleup.domain.notification.listener;

import com.king.paddleup.domain.followers.dto.FollowedEvent;
import com.king.paddleup.domain.notification.NotificationService;
import com.king.paddleup.domain.notification.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class FollowNotificationListener {

    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onFollowed(FollowedEvent event) {
        notificationService.create(
                event.followingId(),
                NotificationType.NEW_FOLLOWER,
                "New follower",
                "@" + event.followerUsername() + " started following you",
                event.followerId()
        );
    }
}
