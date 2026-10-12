package com.king.paddleup.domain.followers.exception;

import com.king.paddleup.shared.exception.DomainException;

public class CannotFollowSelfException extends DomainException {
    public CannotFollowSelfException(String message) {
        super(message);
    }
}
