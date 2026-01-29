package com.stockmarket.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TestInsufficientFundsException {

    @Test
    void shouldStoreMessage() {
        InsufficientFundsException exception = new InsufficientFundsException("missing funds");

        assertEquals("missing funds", exception.getMessage());
    }
}
