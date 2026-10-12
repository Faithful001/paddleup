package com.king.paddleup.domain.comment.exception;

import com.king.paddleup.shared.exception.DomainException;

public class InvalidCommentOperationException extends DomainException {
    public InvalidCommentOperationException(String message) {
        super(message);
    }
}
