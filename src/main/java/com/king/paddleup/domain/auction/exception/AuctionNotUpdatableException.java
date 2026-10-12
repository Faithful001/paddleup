package com.king.paddleup.domain.auction.exception;

import com.king.paddleup.shared.exception.DomainException;

public class AuctionNotUpdatableException extends DomainException {
    public AuctionNotUpdatableException(String message) {
        super(message);
    }
}
