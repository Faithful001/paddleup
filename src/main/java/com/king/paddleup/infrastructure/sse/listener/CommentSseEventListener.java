package com.king.paddleup.infrastructure.sse.listener;

import com.king.paddleup.domain.comment.dto.CommentCreatedEvent;
import com.king.paddleup.infrastructure.sse.CommentSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CommentSseEventListener {

    private final CommentSseService commentSseService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onCommentCreated(CommentCreatedEvent event) {
        commentSseService.sendCommentEvent(event);
    }
}
