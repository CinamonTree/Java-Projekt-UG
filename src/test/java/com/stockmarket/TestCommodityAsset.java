package com.stockmarket;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TestCommodityAsset {

    @Test
    void shouldUseInitialPriceForMarketValue() {
        CommodityAsset asset = new CommodityAsset("OIL", "Oil", 12.0);

        assertEquals(36.0, asset.calculateRealValue(3));
    }

    @Test
    void shouldUseInitialPriceForPurchaseCost() {
        CommodityAsset asset = new CommodityAsset("OIL", "Oil", 12.0);

        assertEquals(36.0, asset.calculatePurchaseCost(3));
    }
}
