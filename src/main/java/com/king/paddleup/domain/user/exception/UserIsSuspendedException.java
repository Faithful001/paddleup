package com.king.paddleup.domain.user.exception;

import com.king.paddleup.shared.exception.DomainException;

public class UserIsSuspendedException extends DomainException {
    public UserIsSuspendedException(String message) {
        super(message);
    }
}
