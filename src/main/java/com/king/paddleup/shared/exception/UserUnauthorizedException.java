package com.king.paddleup.shared.exception;

public class UserUnauthorizedException extends DomainException {
    public UserUnauthorizedException(String message) {
        super(message);
    }
}
