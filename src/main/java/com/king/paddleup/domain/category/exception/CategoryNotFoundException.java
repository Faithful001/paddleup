package com.king.paddleup.domain.category.exception;

import com.king.paddleup.shared.exception.DomainException;

public class CategoryNotFoundException extends DomainException {
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
