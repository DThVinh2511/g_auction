package com.ghtk.auction.exception;

public class ExpiredTokenException extends RuntimeException {

    public ExpiredTokenException(String message) {

        super(message);
    }
}
