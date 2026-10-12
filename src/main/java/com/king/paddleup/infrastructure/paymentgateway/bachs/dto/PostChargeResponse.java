package com.king.paddleup.infrastructure.paymentgateway.bachs.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PostChargeResponse(
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

        @JsonProperty("fees")
        Fees fees,

        @JsonProperty("payment_method")
        String paymentMethod,

        @JsonProperty("checkout_id")
        String checkoutId,

        @JsonProperty("narration")
        String narration,

        @JsonProperty("reference")
        String reference,

        @JsonProperty("billing_reason")
        String billingReason,

        @JsonProperty("customer")
        Customer customer,

        @JsonProperty("created_at")
        String createdAt,

        @JsonProperty("updated_at")
        String updatedAt
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Fees(
            @JsonProperty("amount")
            String amount,

            @JsonProperty("currency")
            String currency
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Customer(
            @JsonProperty("name")
            String name,

            @JsonProperty("email")
            String email
    ) {}
}
