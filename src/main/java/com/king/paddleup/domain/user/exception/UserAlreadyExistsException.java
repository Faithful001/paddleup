package com.king.paddleup.domain.user.exception;

import com.king.paddleup.shared.exception.DomainException;

public class UserAlreadyExistsException extends DomainException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
