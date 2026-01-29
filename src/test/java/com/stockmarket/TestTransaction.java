package com.stockmarket;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.jupiter.api.Test;

class TestTransaction {

    @Test
    void shouldReturnConstructorValues() {
        Date date = new Date();
        Transaction transaction = new Transaction(date, "AAA", AssetType.SHARE, "B-1", "S-1", "B", "S", 5, new BigDecimal("10.00"));

        assertAll(
            () -> assertEquals(date, transaction.getDate()),
            () -> assertEquals("AAA", transaction.getTicker()),
            () -> assertEquals(AssetType.SHARE, transaction.getAssetType()),
            () -> assertEquals("B-1", transaction.getBuyOrderID()),
            () -> assertEquals("S-1", transaction.getSellOrderID()),
            () -> assertEquals("B", transaction.getBuyerPortfolioID()),
            () -> assertEquals("S", transaction.getSellerPortfolioID()),
            () -> assertEquals(5, transaction.getQuantity()),
            () -> assertEquals(new BigDecimal("10.00"), transaction.getUnitPrice())
        );
    }
}
