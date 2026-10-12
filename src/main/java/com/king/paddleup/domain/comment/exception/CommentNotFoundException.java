package com.king.paddleup.domain.comment.exception;

import com.king.paddleup.shared.exception.DomainException;

public class CommentNotFoundException extends DomainException {
    public CommentNotFoundException(String message) {
        super(message);
    }
}
