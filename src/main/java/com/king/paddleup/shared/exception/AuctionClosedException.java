package com.king.paddleup.shared.exception;

public class AuctionClosedException extends DomainException {
    public AuctionClosedException(String message) {
        super(message);
    }
}
