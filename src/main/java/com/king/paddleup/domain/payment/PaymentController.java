package com.king.paddleup.domain.payment;

import com.king.paddleup.domain.payment.dto.InitiatePaymentRequest;
import com.king.paddleup.domain.payment.dto.PaymentResponse;
import com.king.paddleup.shared.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Response<PaymentResponse>> initiatePayment(
            @AuthenticationPrincipal UUID userId,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody InitiatePaymentRequest request
    ) {
        PaymentResponse response = paymentService.initiatePayment(userId, request, idempotencyKey);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Response.success("Payment initiated successfully", response));
    }
}
