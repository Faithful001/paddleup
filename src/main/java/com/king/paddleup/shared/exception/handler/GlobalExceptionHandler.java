package com.king.paddleup.shared.exception.handler;

import com.king.paddleup.shared.exception.*;
import com.king.paddleup.shared.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AuctionNotFoundException.class)
    public ResponseEntity<Response<String>> handleAuctionNotFoundException(AuctionNotFoundException ex) {
        return new ResponseEntity<>(
                Response.error(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(AuctionClosedException.class)
    public ResponseEntity<Response<String>> handleAuctionClosedException(AuctionClosedException ex) {
        return new ResponseEntity<>(
                Response.error(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(InvalidBidException.class)
    public ResponseEntity<Response<String>> handleInvalidBidException(InvalidBidException ex) {
        return new ResponseEntity<>(
                Response.error(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Response<String>> handleUserNotFoundException(UserNotFoundException ex) {
        return new ResponseEntity<>(
                Response.error(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(BidTooLowException.class)
    public ResponseEntity<Response<String>> handleBidTooLowException(BidTooLowException ex) {
        return new ResponseEntity<>(
                Response.error(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }



}
