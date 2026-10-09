package com.king.paddleup.domain.notification.listener;

import com.king.paddleup.domain.auction.Auction;
import com.king.paddleup.domain.auction.AuctionRepository;
import com.king.paddleup.domain.comment.CommentRepository;
import com.king.paddleup.domain.comment.dto.CommentCreatedEvent;
import com.king.paddleup.domain.notification.NotificationService;
import com.king.paddleup.domain.notification.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentNotificationListener {

    private final NotificationService notificationService;
    private final AuctionRepository auctionRepository;
    private final CommentRepository commentRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onCommentCreated(CommentCreatedEvent event) {
        Optional<Auction> auctionOpt = auctionRepository.findById(event.auctionId());
        if (auctionOpt.isEmpty()) {
            log.warn("Could not send comment notification: auction not found for event {}", event);
            return;
        }

        Auction auction = auctionOpt.get();

        if (event.parentId() == null) {
            // Top-level comment: notify auction seller (unless seller is the commenter)
            if (!event.authorId().equals(auction.getSeller().getId())) {
                notificationService.create(
                        auction.getSeller().getId(),
                        NotificationType.COMMENT_POSTED,
                        "New comment on your auction",
                        "@" + event.authorUsername() + " commented on '" + auction.getTitle() + "'",
                        auction.getId()
                );
            }
        } else {
            // Reply: notify author of the parent comment
            commentRepository.findById(event.parentId()).ifPresent(parentComment -> {
                if (!event.authorId().equals(parentComment.getAuthor().getId())) {
                    String body = event.isSeller()
                            ? "The seller replied to your comment on '" + auction.getTitle() + "'"
                            : "@" + event.authorUsername() + " replied to your comment on '" + auction.getTitle() + "'";

                    notificationService.create(
                            parentComment.getAuthor().getId(),
                            NotificationType.COMMENT_REPLIED,
                            "New reply to your comment",
                            body,
                            auction.getId()
                    );
                }
            });
        }
    }
}
