package com.king.paddleup.domain.auction.exception;

import com.king.paddleup.shared.exception.DomainException;

public class AuctionNotFoundException extends DomainException {
    public AuctionNotFoundException(String message) {
        super(message);
    }
}
