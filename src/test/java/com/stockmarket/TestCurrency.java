package com.stockmarket;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TestCurrency {

    @Test
    void shouldReturnConstructorValues() {
        Currency currency = new Currency("EUR", "Euro", 4.5, 0.02);

        assertAll(
            () -> assertEquals(AssetType.CURRENCY, currency.getType()),
            () -> assertEquals("EUR", currency.getID()),
            () -> assertEquals("Euro", currency.getName()),
            () -> assertEquals(4.5, currency.getInitialPrice()),
            () -> assertEquals(0.02, currency.getSpreadPercent())
        );
    }

    @Test
    void shouldApplySpreadForMarketValue() {
        Currency currency = new Currency("EUR", "Euro", 10.0, 0.1);

        assertEquals(27.0, currency.calculateRealValue(3));
    }

    @Test
    void shouldApplySpreadForPurchaseCost() {
        Currency currency = new Currency("EUR", "Euro", 10.0, 0.1);

        assertEquals(33.0, currency.calculatePurchaseCost(3));
    }
}
