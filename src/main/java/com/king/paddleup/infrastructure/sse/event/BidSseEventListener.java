package com.king.paddleup.infrastructure.sse.event;

import com.king.paddleup.domain.bid.dto.BidPlacedEvent;
import com.king.paddleup.infrastructure.sse.BidSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BidSseEventListener {
    private final BidSseService bidSseService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(BidPlacedEvent event) {
        bidSseService.sendBidEvent(event);
    }
}
