package com.stockmarket.exceptions;

public class InsufficientAssetsException extends RuntimeException {
    public InsufficientAssetsException(String message) {
        super(message);
    }
}
