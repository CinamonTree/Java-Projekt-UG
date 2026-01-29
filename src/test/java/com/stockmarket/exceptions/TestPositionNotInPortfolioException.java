package com.stockmarket.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TestPositionNotInPortfolioException {

    @Test
    void shouldStoreMessage() {
        PositionNotInPortfolioException exception = new PositionNotInPortfolioException("missing position");

        assertEquals("missing position", exception.getMessage());
    }
}
