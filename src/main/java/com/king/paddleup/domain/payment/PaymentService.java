package com.king.paddleup.domain.payment;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.king.paddleup.domain.auction.Auction;
import com.king.paddleup.domain.auction.AuctionRepository;
import com.king.paddleup.domain.auction.enums.AuctionStatus;
import com.king.paddleup.domain.payment.dto.InitiatePaymentRequest;
import com.king.paddleup.domain.payment.dto.PaymentResponse;
import com.king.paddleup.domain.payment.enums.PaymentStatus;
import com.king.paddleup.domain.user.User;
import com.king.paddleup.domain.user.UserRepository;
import com.king.paddleup.domain.idempotency.IdempotencyService;
import com.king.paddleup.infrastructure.paymentgateway.PaymentGatewayProvider;
import com.king.paddleup.infrastructure.paymentgateway.PaymentGatewayProviderFactory;
import com.king.paddleup.infrastructure.paymentgateway.bachs.dto.PostChargeRequest;
import com.king.paddleup.infrastructure.paymentgateway.bachs.dto.PostChargeResponse;
import com.king.paddleup.infrastructure.paymentgateway.bachs.dto.BachsWebhookEvent;
import com.king.paddleup.domain.auction.exception.AuctionNotFoundException;
import com.king.paddleup.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final PaymentGatewayProviderFactory paymentGatewayProviderFactory;
    private final IdempotencyService idempotencyService;
    private final ObjectMapper objectMapper;

    public PaymentResponse initiatePayment(UUID userId, InitiatePaymentRequest request, String idempotencyKey) {
        return idempotencyService.executeIdempotently(
                idempotencyKey,
                "PAYMENT_CHARGE",
                request,
                PaymentResponse.class,
                () -> doInitiatePayment(userId, request, idempotencyKey)
        );
    }

    @Transactional
    protected PaymentResponse doInitiatePayment(UUID userId, InitiatePaymentRequest request, String idempotencyKey) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        Auction auction = null;
        if (request.auctionId() != null) {
            auction = auctionRepository.findById(request.auctionId())
                    .orElseThrow(() -> new AuctionNotFoundException("Auction not found with id: " + request.auctionId()));
        }

        String reference = "PUP-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        String currency = (request.currency() != null && !request.currency().isBlank())
                ? request.currency().toUpperCase()
                : "USD";

        String amountString = request.amount().setScale(2, RoundingMode.HALF_UP).toPlainString();
        String description = request.description() != null ? request.description() : "Payment for " + reference;

        @SuppressWarnings("unchecked")
        PaymentGatewayProvider<PostChargeRequest> provider =
                (PaymentGatewayProvider<PostChargeRequest>) paymentGatewayProviderFactory.getProvider();

        PostChargeRequest chargeRequest = new PostChargeRequest(
                user.getEmail(),
                amountString,
                currency,
                description,
                reference
        );

        PostChargeResponse chargeResponse = provider.pay(user, chargeRequest, idempotencyKey);

        PaymentStatus paymentStatus = mapStatus(chargeResponse.status());

        String rawResponse = null;
        try {
            rawResponse = objectMapper.writeValueAsString(chargeResponse);
        } catch (JacksonException e) {
            log.warn("Failed to serialize chargeResponse to JSON string", e);
        }

        Payment payment = Payment.builder()
                .user(user)
                .auction(auction)
                .provider(provider.getProviderName().name())
                .paymentId(chargeResponse.paymentId())
                .reference(reference)
                .idempotencyKey(idempotencyKey)
                .amount(request.amount())
                .currency(currency)
                .status(paymentStatus)
                .description(description)
                .rawResponse(rawResponse)
                .build();

        Payment saved = paymentRepository.save(payment);

        return toPaymentResponse(saved);
    }

    @Transactional
    public void processBachsWebhook(BachsWebhookEvent event) {
        if (event == null || event.data() == null) {
            log.warn("Received empty Bachs webhook payload");
            return;
        }

        String paymentId = event.data().paymentId();
        String reference = event.data().reference();
        String eventType = event.type();

        log.info("Processing Bachs webhook: eventId={}, type={}, paymentId={}, reference={}",
                event.id(), eventType, paymentId, reference);

        Optional<Payment> paymentOpt = Optional.empty();
        if (paymentId != null && !paymentId.isBlank()) {
            paymentOpt = paymentRepository.findByPaymentId(paymentId); // deduplicate payment record (avoid duplicates)
        }
        if (paymentOpt.isEmpty() && reference != null && !reference.isBlank()) {
            paymentOpt = paymentRepository.findByReference(reference);
        }

        if (paymentOpt.isEmpty()) {
            log.warn("No local payment found matching Bachs event: paymentId={}, reference={}", paymentId, reference);
            return;
        }

        Payment payment = paymentOpt.get();

        if ("collection.succeeded".equalsIgnoreCase(eventType)) {
            payment.setStatus(PaymentStatus.SUCCEEDED);
            if (payment.getAuction() != null) {
                Auction auction = payment.getAuction();
                if (auction.getStatus() == AuctionStatus.AWAITING_PAYMENT) {
                    auction.setStatus(AuctionStatus.COMPLETED);
                    auctionRepository.save(auction);
                    log.info("Auction {} marked COMPLETED following payment settlement", auction.getId());
                }
            }
        } else if ("collection.failed".equalsIgnoreCase(eventType)) {
            payment.setStatus(PaymentStatus.FAILED);
        } else if ("collection.underpaid".equalsIgnoreCase(eventType)) {
            payment.setStatus(PaymentStatus.UNDERPAID);
        } else {
            log.info("Unhandled Bachs webhook event type: {}", eventType);
            return;
        }

        paymentRepository.save(payment);
        log.info("Payment {} status updated to {}", payment.getId(), payment.getStatus());
    }

    private PaymentStatus mapStatus(String bachsStatus) {
        if (bachsStatus == null) {
            return PaymentStatus.PENDING;
        }
        return switch (bachsStatus.toLowerCase()) {
            case "succeeded", "success", "paid" -> PaymentStatus.SUCCEEDED;
            case "failed" -> PaymentStatus.FAILED;
            case "underpaid" -> PaymentStatus.UNDERPAID;
            case "cancelled" -> PaymentStatus.CANCELLED;
            case "processing" -> PaymentStatus.PROCESSING;
            default -> PaymentStatus.PENDING;
        };
    }

    private PaymentResponse toPaymentResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getPaymentId(),
                payment.getReference(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus().name(),
                payment.getDescription(),
                payment.getCreatedAt()
        );
    }
}
