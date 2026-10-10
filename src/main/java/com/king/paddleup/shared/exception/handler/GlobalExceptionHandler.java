package com.king.paddleup.shared.exception.handler;

import com.king.paddleup.shared.exception.*;
import com.king.paddleup.shared.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<Response<String>> handleFileUploadException(FileUploadException ex) {
        return new ResponseEntity<>(
                Response.error(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Response<String>> handleDomainxception(DomainException ex) {
        return new ResponseEntity<>(
                Response.error(ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(AuctionNotFoundException.class)
    public ResponseEntity<Response<String>> handleAuctionNotFoundException(AuctionNotFoundException ex) {
        return new ResponseEntity<>(
                Response.error(ex.getMessage()),
                HttpStatus.NOT_FOUND
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
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(UserIsSuspendedException.class)
    public ResponseEntity<Response<String>> handleUserIsSuspendedException(UserIsSuspendedException ex) {
        return new ResponseEntity<>(
                Response.error(ex.getMessage()),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(BidTooLowException.class)
    public ResponseEntity<Response<String>> handleBidTooLowException(BidTooLowException ex) {
        return new ResponseEntity<>(
                Response.error(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Response<String>> handleCategoryNotFoundException(CategoryNotFoundException ex) {
        return new ResponseEntity<>(
                Response.error(ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(AuctionNotUpdatableException.class)
    public ResponseEntity<Response<String>> handleAuctionNotUpdatableException(AuctionNotUpdatableException ex) {
        return new ResponseEntity<>(
                Response.error(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(UserUnauthorizedException.class)
    public ResponseEntity<Response<String>> handleUserUnauthorizedException(UserUnauthorizedException ex) {
        return new ResponseEntity<>(
                Response.error(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(EmailDeliveryException.class)
    public ResponseEntity<Response<String>> handleEmailDeliveryException(EmailDeliveryException ex) {
        return new ResponseEntity<>(
                Response.error(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Response<String>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return new ResponseEntity<>(
                Response.error(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Response<String>> handleInvalidTokenException(InvalidTokenException ex) {
        return new ResponseEntity<>(
                Response.error(ex.getMessage()),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(CannotFollowSelfException.class)
    public ResponseEntity<Response<String>> handleCannotFollowSelfException(CannotFollowSelfException ex) {
        return new ResponseEntity<>(
                Response.error(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<Response<String>> handleCommentNotFoundException(CommentNotFoundException ex) {
        return new ResponseEntity<>(
                Response.error(ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(InvalidCommentOperationException.class)
    public ResponseEntity<Response<String>> handleInvalidCommentOperationException(InvalidCommentOperationException ex) {
        return new ResponseEntity<>(
                Response.error(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(InvalidLikeOperationException.class)
    public ResponseEntity<Response<String>> handleInvalidLikeOperationException(InvalidLikeOperationException ex) {
        return new ResponseEntity<>(
                Response.error(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<Response<String>> handleNotificationNotFoundException(NotificationNotFoundException ex) {
        return new ResponseEntity<>(
                Response.error(ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }
}
