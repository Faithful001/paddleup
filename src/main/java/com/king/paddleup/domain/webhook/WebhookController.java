package com.king.paddleup.domain.webhook;

import com.king.paddleup.domain.payment.service.PaymentService;
import com.king.paddleup.domain.webhook.dto.BachsWebhookEvent;
import com.king.paddleup.infrastructure.paymentgateway.bachs.BachsWebhookVerifier;
import com.king.paddleup.shared.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/webhooks")
public class WebhookController {

    private final BachsWebhookVerifier webhookVerifier;
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @PostMapping("/bachs")
    public ResponseEntity<Response<Void>> handleBachsWebhook(
            @RequestBody String rawBody,
            @RequestHeader(value = "X-Bachs-Signature", required = false) String signatureV1,
            @RequestHeader(value = "X-Bachs-Signature-V2", required = false) String signatureV2,
            @RequestHeader(value = "X-Bachs-Timestamp", required = false) String timestamp
    ) {
        String signature = (signatureV2 != null && !signatureV2.isBlank()) ? signatureV2 : signatureV1;

        if (!webhookVerifier.verifySignature(rawBody, signature, timestamp)) {
            log.warn("Bachs webhook signature verification failed");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Response.error("Invalid webhook signature"));
        }

        try {
            BachsWebhookEvent event = objectMapper.readValue(rawBody, BachsWebhookEvent.class);

            log.info("Received Bachs webhook: eventId={}, type={}", event.id(), event.type());

            paymentService.processBachsWebhook(event);

            return ResponseEntity.ok(Response.message("Webhook processed successfully"));
        } catch (Exception ex) {
            log.error("Failed to process Bachs webhook", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.error("Webhook processing failed"));
        }
    }
}
