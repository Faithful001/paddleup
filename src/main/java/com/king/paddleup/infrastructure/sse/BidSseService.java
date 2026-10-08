package com.king.paddleup.infrastructure.sse;

import com.king.paddleup.domain.bid.dto.BidPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class BidSseService {

    private static final Long DEFAULT_TIMEOUT = 30 * 60 * 1000L; // 30 minutes

    private final Map<UUID, List<SseEmitter>> auctionEmitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(UUID auctionId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        auctionEmitters.computeIfAbsent(auctionId, k -> new CopyOnWriteArrayList<SseEmitter>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(auctionId, emitter));
        emitter.onTimeout(() -> {
            emitter.complete();
            removeEmitter(auctionId, emitter);
        });
        emitter.onError(e -> {
            log.debug("SSE error for auction {}: {}", auctionId, e.getMessage());
            removeEmitter(auctionId, emitter);
        });

        try {
            emitter.send(SseEmitter.event()
                    .name("INIT")
                    .data("Connected to bid stream for auction " + auctionId, MediaType.TEXT_PLAIN));
        } catch (IOException e) {
            emitter.completeWithError(e);
            removeEmitter(auctionId, emitter);
        }

        return emitter;
    }

    public void sendBidEvent(BidPlacedEvent event) {
        List<SseEmitter> emitters = auctionEmitters.get(event.auctionId());
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("bid-placed")
                        .data(event, MediaType.APPLICATION_JSON));
            } catch (IOException | IllegalStateException e) {
                log.debug("Failed to send SSE event for auction {}: {}", event.auctionId(), e.getMessage());
                emitter.complete();
                removeEmitter(event.auctionId(), emitter);
            }
        }
    }

    private void removeEmitter(UUID auctionId, SseEmitter emitter) {
        List<SseEmitter> emitters = auctionEmitters.get(auctionId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                auctionEmitters.remove(auctionId);
            }
        }
    }
}
