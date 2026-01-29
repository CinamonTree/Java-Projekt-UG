package com.stockmarket.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TestInsufficientAssetsException {

    @Test
    void shouldStoreMessage() {
        InsufficientAssetsException exception = new InsufficientAssetsException("missing assets");

        assertEquals("missing assets", exception.getMessage());
    }
}
