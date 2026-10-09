package com.king.paddleup.shared.exception;

public class CannotFollowSelfException extends DomainException {
    public CannotFollowSelfException(String message) {
        super(message);
    }
}
