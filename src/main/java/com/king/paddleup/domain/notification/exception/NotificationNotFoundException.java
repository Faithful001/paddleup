package com.king.paddleup.domain.notification.exception;

import com.king.paddleup.shared.exception.DomainException;

public class NotificationNotFoundException extends DomainException {
    public NotificationNotFoundException(String message) {
        super(message);
    }
}
