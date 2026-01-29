package com.stockmarket.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TestDataIntegrityException {

    @Test
    void shouldStoreMessage() {
        DataIntegrityException exception = new DataIntegrityException("bad data");

        assertEquals("bad data", exception.getMessage());
    }
}
