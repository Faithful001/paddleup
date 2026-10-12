package com.king.paddleup.domain.like.exception;

import com.king.paddleup.shared.exception.DomainException;

public class InvalidLikeOperationException extends DomainException {
    public InvalidLikeOperationException(String message) {
        super(message);
    }
}
