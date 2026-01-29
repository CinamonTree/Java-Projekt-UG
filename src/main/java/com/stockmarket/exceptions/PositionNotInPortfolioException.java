package com.stockmarket.exceptions;

public class PositionNotInPortfolioException extends RuntimeException {
    public PositionNotInPortfolioException(String message) {
        super(message);
    }
}
