package com.king.paddleup.infrastructure.event;

import com.king.paddleup.domain.bid.dto.BidPlacedEvent;
import com.king.paddleup.infrastructure.sse.BidSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BidEventListener {
    private final BidSseService bidSseService;

    @EventListener
    void on(BidPlacedEvent event) {
        bidSseService.sendBidEvent(event);
    }
}

