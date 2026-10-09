package com.king.paddleup.domain.notification.listener;

import com.king.paddleup.domain.like.dto.AuctionLikedEvent;
import com.king.paddleup.domain.notification.NotificationService;
import com.king.paddleup.domain.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeNotificationListener {

    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onAuctionLiked(AuctionLikedEvent event) {
        notificationService.create(
                event.sellerId(),
                NotificationType.AUCTION_LIKED,
                "Someone liked your auction",
                "@" + event.likerUsername() + " liked your auction '" + event.auctionTitle() + "'",
                event.auctionId()
        );
    }
}
