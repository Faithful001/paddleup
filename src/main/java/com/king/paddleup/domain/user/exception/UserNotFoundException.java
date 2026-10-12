package com.king.paddleup.domain.user.exception;

import com.king.paddleup.shared.exception.DomainException;

public class UserNotFoundException extends DomainException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
