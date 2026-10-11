package com.king.paddleup.infrastructure.paymentgateway.bachs.dto;

//{
//        "payment_id": "ch_389305e973a841cc",
//        "status": "processing",
//        "amount": "30.85",
//        "amount_paid": "0.00",
//        "amount_remaining": "30.85",
//        "currency": "USD",
//        "fees": {
//        "amount": "1.85",
//        "currency": "USD"
//        },
//        "payment_method": "CARD",
//        "checkout_id": null,
//        "narration": "April usage",
//        "reference": "INV-2026-04-881",
//        "billing_reason": "purchase",
//        "customer": {
//        "name": "Jane Doe",
//        "email": "jane@example.com"
//        },
//        "created_at": "2026-04-27T12:05:00.000Z",
//        "updated_at": "2026-04-27T12:05:00.000Z"
//        }

public record PostChargeResponse(
        String payment_id,
        String status,
        String amount,
        String amount_remaning,
        String currency,
        Fees fees,
        String payment_method,
        String checkout_id,
        String narration,
        String reference,
        String billing_reason,
        Customer customer,
        String created_at,
        String updated_at

) {
    public record Fees(
            String amount,
            String currency
    ){}

    public record Customer(
            String name,
            String email
    ) {}
}
