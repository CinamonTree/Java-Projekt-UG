package com.stockmarket;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

class TestOrderType {

    @Test
    void shouldContainAllExpectedValues() {
        assertArrayEquals(new OrderType[] {OrderType.MARKET, OrderType.LIMIT}, OrderType.values());
    }
}
