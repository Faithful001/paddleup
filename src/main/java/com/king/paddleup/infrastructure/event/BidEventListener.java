package com.king.paddleup.infrastructure.event;

import com.king.paddleup.domain.bid.dto.BidPlacedEvent;
import com.king.paddleup.infrastructure.websocket.BidPlacedPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BidEventListener {
    private final BidPlacedPublisher bidPlacedPublisher;

    @EventListener
    void on(BidPlacedEvent event) {
        bidPlacedPublisher.publish(event);
    }
}
