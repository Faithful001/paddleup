package com.king.paddleup.domain.bid.exception;

import com.king.paddleup.shared.exception.DomainException;

public class BidTooLowException extends DomainException {
    public BidTooLowException(String message) {
        super(message);
    }
}
