package com.king.paddleup.infrastructure.paymentgateway.bachs.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BachsWebhookData(
        @JsonProperty("payment_id")
        String paymentId,

        @JsonProperty("status")
        String status,

        @JsonProperty("amount")
        String amount,

        @JsonProperty("amount_paid")
        String amountPaid,

        @JsonProperty("amount_remaining")
        String amountRemaining,

        @JsonProperty("currency")
        String currency,

        @JsonProperty("reference")
        String reference,

        @JsonProperty("payment_method")
        String paymentMethod,

        @JsonProperty("narration")
        String narration,

        @JsonProperty("customer")
        Customer customer
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Customer(
            @JsonProperty("name")
            String name,

            @JsonProperty("email")
            String email
    ) {}
}
