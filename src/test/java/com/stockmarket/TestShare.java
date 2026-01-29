package com.stockmarket;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TestShare {

    @Test
    void shouldUseInitialPriceForMarketValue() {
        Share share = new Share("AAA", "Acme", 12.0);

        assertEquals(36.0, share.calculateRealValue(3));
    }

    @Test
    void shouldUseInitialPriceForPurchaseCost() {
        Share share = new Share("AAA", "Acme", 12.0);

        assertEquals(36.0, share.calculatePurchaseCost(3));
    }
}
