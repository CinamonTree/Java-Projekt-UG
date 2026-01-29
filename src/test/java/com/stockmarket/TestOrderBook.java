package com.stockmarket;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

class TestOrderBook {

    @Test
    void shouldMatchMarketOrdersWhenBothPresent() {
        OrderBook orderBook = new OrderBook("AAA", AssetType.SHARE);
        orderBook.placeBuyMarketOrder("BUYER", 3);
        orderBook.placeSellMarketOrder("SELLER", 3);

        ArrayList<Transaction> transactions = orderBook.process();

        Transaction transaction = transactions.get(0);
        assertAll(
            () -> assertEquals(1, transactions.size()),
            () -> assertEquals(3, transaction.getQuantity()),
            () -> assertEquals(BigDecimal.ZERO, transaction.getUnitPrice()),
            () -> assertEquals("BUYER", transaction.getBuyerPortfolioID()),
            () -> assertEquals("SELLER", transaction.getSellerPortfolioID()),
            () -> assertTrue(transaction.getBuyOrderID().startsWith("AAA-")),
            () -> assertTrue(transaction.getSellOrderID().startsWith("AAA-"))
        );
    }

    @Test
    void shouldMatchLimitOrdersWhenBuyPriceMeetsSellPrice() {
        OrderBook orderBook = new OrderBook("AAA", AssetType.SHARE);
        orderBook.placeBuyLimitOrder("BUYER", new BigDecimal("10.00"), 5);
        orderBook.placeSellLimitOrder("SELLER", new BigDecimal("8.00"), 5);

        ArrayList<Transaction> transactions = orderBook.process();

        Transaction transaction = transactions.get(0);
        assertAll(
            () -> assertEquals(1, transactions.size()),
            () -> assertEquals(new BigDecimal("8.00"), transaction.getUnitPrice())
        );
    }

    @Test
    void shouldNotMatchLimitOrdersWhenBuyPriceBelowSellPrice() {
        OrderBook orderBook = new OrderBook("AAA", AssetType.SHARE);
        orderBook.placeBuyLimitOrder("BUYER", new BigDecimal("7.00"), 5);
        orderBook.placeSellLimitOrder("SELLER", new BigDecimal("8.00"), 5);

        ArrayList<Transaction> transactions = orderBook.process();

        assertEquals(0, transactions.size());
    }

    @Test
    void shouldUseSellLimitPriceWhenBuyIsMarket() {
        OrderBook orderBook = new OrderBook("AAA", AssetType.SHARE);
        orderBook.placeBuyMarketOrder("BUYER", 2);
        orderBook.placeSellLimitOrder("SELLER", new BigDecimal("9.50"), 2);

        ArrayList<Transaction> transactions = orderBook.process();

        assertEquals(new BigDecimal("9.50"), transactions.get(0).getUnitPrice());
    }

    @Test
    void shouldUseBuyLimitPriceWhenSellIsMarket() {
        OrderBook orderBook = new OrderBook("AAA", AssetType.SHARE);
        orderBook.placeBuyLimitOrder("BUYER", new BigDecimal("11.00"), 2);
        orderBook.placeSellMarketOrder("SELLER", 2);

        ArrayList<Transaction> transactions = orderBook.process();

        assertEquals(new BigDecimal("11.00"), transactions.get(0).getUnitPrice());
    }

    @Test
    void shouldKeepRemainingQuantityForNextProcess() {
        OrderBook orderBook = new OrderBook("AAA", AssetType.SHARE);
        orderBook.placeBuyLimitOrder("BUYER", new BigDecimal("10.00"), 5);
        orderBook.placeSellLimitOrder("SELLER", new BigDecimal("9.00"), 10);

        ArrayList<Transaction> first = orderBook.process();
        orderBook.placeBuyMarketOrder("BUYER2", 5);
        ArrayList<Transaction> second = orderBook.process();

        assertAll(
            () -> assertEquals(1, first.size()),
            () -> assertEquals(5, first.get(0).getQuantity()),
            () -> assertEquals(1, second.size()),
            () -> assertEquals(5, second.get(0).getQuantity()),
            () -> assertEquals(new BigDecimal("9.00"), second.get(0).getUnitPrice())
        );
    }
}
