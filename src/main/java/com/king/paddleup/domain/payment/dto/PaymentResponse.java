package com.king.paddleup.domain.payment.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        String paymentId,
        String reference,
        BigDecimal amount,
        String currency,
        String status,
        String description,
        Instant createdAt
) {}
