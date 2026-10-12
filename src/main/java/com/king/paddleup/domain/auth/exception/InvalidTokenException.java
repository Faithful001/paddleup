package com.king.paddleup.domain.auth.exception;

import com.king.paddleup.shared.exception.DomainException;

public class InvalidTokenException extends DomainException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
