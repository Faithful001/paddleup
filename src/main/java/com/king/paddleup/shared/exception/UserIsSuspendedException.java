package com.king.paddleup.shared.exception;

public class UserIsSuspendedException extends DomainException {
    public UserIsSuspendedException(String message) {
        super(message);
    }
}
