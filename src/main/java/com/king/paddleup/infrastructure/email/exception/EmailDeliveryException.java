package com.king.paddleup.infrastructure.email.exception;

import com.king.paddleup.shared.exception.DomainException;

public class EmailDeliveryException extends DomainException {
    public EmailDeliveryException(String message) {
        super(message);
    }
}
