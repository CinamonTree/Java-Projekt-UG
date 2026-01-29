package com.stockmarket;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

class TestOrderSide {

    @Test
    void shouldContainAllExpectedValues() {
        assertArrayEquals(new OrderSide[] {OrderSide.BUY, OrderSide.SELL}, OrderSide.values());
    }
}
