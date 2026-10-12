package com.king.paddleup.domain.auction.exception;

import com.king.paddleup.shared.exception.DomainException;

public class AuctionClosedException extends DomainException {
    public AuctionClosedException(String message) {
        super(message);
    }
}
