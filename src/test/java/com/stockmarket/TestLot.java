package com.stockmarket;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.jupiter.api.Test;

class TestLot {

    @Test
    void shouldReturnConstructorValues() {
        Date date = new Date();
        BigDecimal price = new BigDecimal("12.34");
        Lot lot = new Lot(date, price, 5);

        assertAll(
            () -> assertEquals(date, lot.getTransactionDate()),
            () -> assertEquals(price, lot.getUnitPrice()),
            () -> assertEquals(5, lot.getQuantity())
        );
    }

    @Test
    void shouldReduceQuantity() {
        Lot lot = new Lot(new Date(), new BigDecimal("10.00"), 5);

        lot.reduceQuantity(2);

        assertEquals(3, lot.getQuantity());
    }

    @Test
    void shouldRejectNonPositiveReduction() {
        Lot lot = new Lot(new Date(), new BigDecimal("10.00"), 5);

        assertThrows(IllegalArgumentException.class, () -> lot.reduceQuantity(0));
    }

    @Test
    void shouldRejectReductionAboveQuantity() {
        Lot lot = new Lot(new Date(), new BigDecimal("10.00"), 5);

        assertThrows(IllegalArgumentException.class, () -> lot.reduceQuantity(6));
    }
}
