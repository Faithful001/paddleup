package com.king.paddleup.domain.bid.exception;

import com.king.paddleup.shared.exception.DomainException;

public class InvalidBidException extends DomainException {
    public InvalidBidException(String message) {
        super(message);
    }
}
