package com.king.paddleup.infrastructure.websocket;

import com.king.paddleup.domain.comment.dto.CommentCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentCreatedPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void onCommentCreated(CommentCreatedEvent event) {
        messagingTemplate.convertAndSend(
                "/topic/auction/" + event.auctionId() + "/comments",
                event
        );
    }
}
