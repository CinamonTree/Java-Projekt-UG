package com.stockmarket;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class TestOrder {

    @Test
    void shouldReturnConstructorValues() {
        Order order = new Order("ORD-1", "P1", OrderSide.BUY, OrderType.LIMIT, 5, new BigDecimal("10.00"), 3);

        assertAll(
            () -> assertEquals("ORD-1", order.getOrderID()),
            () -> assertEquals("P1", order.getPortfolioID()),
            () -> assertEquals(OrderSide.BUY, order.getOrderSide()),
            () -> assertEquals(OrderType.LIMIT, order.getOrderType()),
            () -> assertEquals(5, order.getQuantity()),
            () -> assertEquals(new BigDecimal("10.00"), order.getLimitPrice()),
            () -> assertEquals(3, order.getSequence())
        );
    }

    @Test
    void shouldReduceQuantity() {
        Order order = new Order("ORD-1", "P1", OrderSide.BUY, OrderType.LIMIT, 5, new BigDecimal("10.00"), 3);

        order.reduceQuantity(2);

        assertEquals(3, order.getQuantity());
    }

    @Test
    void shouldRejectNonPositiveReduction() {
        Order order = new Order("ORD-1", "P1", OrderSide.BUY, OrderType.LIMIT, 5, new BigDecimal("10.00"), 3);

        assertThrows(IllegalArgumentException.class, () -> order.reduceQuantity(0));
    }

    @Test
    void shouldRejectReductionAboveQuantity() {
        Order order = new Order("ORD-1", "P1", OrderSide.BUY, OrderType.LIMIT, 5, new BigDecimal("10.00"), 3);

        assertThrows(IllegalArgumentException.class, () -> order.reduceQuantity(6));
    }
}
