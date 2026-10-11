package com.king.paddleup.infrastructure.paymentgateway.bachs.dto;

public record PostChargeRequest(
        String customer,
        String amount,
        String currency,
        String description,
        String reference
) {
}
