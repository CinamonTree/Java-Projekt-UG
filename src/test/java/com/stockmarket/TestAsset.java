package com.stockmarket;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class TestAsset {

    private static class TestableAsset extends Asset {
        TestableAsset(AssetType type, String symbol, String name, double initialPrice) {
            super(type, symbol, name, initialPrice);
        }

        @Override
        public double calculateRealValue(int quantity) {
            return getInitialPrice() * quantity;
        }

        @Override
        public double calculatePurchaseCost(int quantity) {
            return getInitialPrice() * quantity;
        }
    }

    @Test
    void shouldReturnConstructorValues() {
        TestableAsset asset = new TestableAsset(AssetType.SHARE, "AAA", "Test", 12.5);

        assertAll(
            () -> assertEquals(AssetType.SHARE, asset.getType()),
            () -> assertEquals("AAA", asset.getID()),
            () -> assertEquals("Test", asset.getName()),
            () -> assertEquals(12.5, asset.getInitialPrice())
        );
    }

    @Test
    void shouldTreatAssetsWithSameTypeAndIdAsEqual() {
        TestableAsset left = new TestableAsset(AssetType.SHARE, "AAA", "One", 10.0);
        TestableAsset right = new TestableAsset(AssetType.SHARE, "AAA", "Two", 25.0);

        assertAll(
            () -> assertEquals(left, right),
            () -> assertEquals(left.hashCode(), right.hashCode())
        );
    }

    @Test
    void shouldTreatAssetsWithDifferentIdAsNotEqual() {
        TestableAsset left = new TestableAsset(AssetType.SHARE, "AAA", "One", 10.0);
        TestableAsset right = new TestableAsset(AssetType.SHARE, "BBB", "One", 10.0);

        assertNotEquals(left, right);
    }

    @Test
    void shouldTreatAssetsWithDifferentTypeAsNotEqual() {
        TestableAsset left = new TestableAsset(AssetType.SHARE, "AAA", "One", 10.0);
        TestableAsset right = new TestableAsset(AssetType.COMMODITY, "AAA", "One", 10.0);

        assertNotEquals(left, right);
    }
}
