package com.king.paddleup.domain.user.exception;

import com.king.paddleup.shared.exception.DomainException;

public class UserUnauthorizedException extends DomainException {
    public UserUnauthorizedException(String message) {
        super(message);
    }
}
