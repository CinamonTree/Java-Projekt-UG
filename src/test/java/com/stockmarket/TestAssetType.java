package com.stockmarket;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

class TestAssetType {

    @Test
    void shouldContainAllExpectedValues() {
        assertArrayEquals(new AssetType[] {AssetType.SHARE, AssetType.COMMODITY, AssetType.CURRENCY}, AssetType.values());
    }
}
