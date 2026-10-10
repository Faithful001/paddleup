package com.king.paddleup.infrastructure.websocket;

import com.king.paddleup.domain.bid.dto.BidPlacedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BidPlacedPublisher {
    private final SimpMessagingTemplate messagingTemplate;

    public void publish(BidPlacedEvent bid) {
        messagingTemplate.convertAndSend("/topic/auction/" + bid.auctionId() + "/bids", bid);
    }
}
