package com.stockmarket;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.stockmarket.exceptions.InsufficientAssetsException;

class TestTradingService {

    @Test
    void shouldRejectSellWhenPortfolioLacksAssets() {
        TradingService service = new TradingService();
        service.registerInstrument(new Instrument(new Share("AAA", "Acme", 10.0), "AAA", Currency.getInstance("USD")));
        Portfolio portfolio = new Portfolio("P1", Money.from(new BigDecimal("100.00"), Currency.getInstance("USD")));
        service.registerPortfolio("P1", portfolio);

        assertThrows(InsufficientAssetsException.class, () -> service.placeSellMarketOrder("AAA", "P1", 1));
    }

    @Test
    void shouldRejectOrderForUnknownTicker() {
        TradingService service = new TradingService();
        Portfolio portfolio = new Portfolio("P1", Money.from(new BigDecimal("100.00"), Currency.getInstance("USD")));
        service.registerPortfolio("P1", portfolio);

        assertThrows(IllegalArgumentException.class, () -> service.placeBuyMarketOrder("AAA", "P1", 1));
    }

    @Test
    void shouldRejectBuyLimitWhenPortfolioMissing() {
        TradingService service = new TradingService();
        service.registerInstrument(new Instrument(new Share("AAA", "Acme", 10.0), "AAA", Currency.getInstance("USD")));

        assertThrows(IllegalArgumentException.class,
            () -> service.placeBuyLimitOrder("AAA", "MISSING", new BigDecimal("10.00"), 1));
    }

    @Test
    void shouldApplyTransactionsToPortfolios() {
        TradingService service = new TradingService();
        service.registerInstrument(new Instrument(new Share("AAA", "Acme", 10.0), "AAA", Currency.getInstance("USD")));

        Portfolio buyer = new Portfolio("BUYER", Money.from(new BigDecimal("100.00"), Currency.getInstance("USD")));
        Portfolio seller = new Portfolio("SELLER", Money.from(BigDecimal.ZERO, Currency.getInstance("USD")));
        Position position = new Position("AAA", AssetType.SHARE);
        position.addLot(new Lot(new Date(), new BigDecimal("5.00"), 2));
        seller.addPosition(position);

        service.registerPortfolio("BUYER", buyer);
        service.registerPortfolio("SELLER", seller);

        service.placeBuyLimitOrder("AAA", "BUYER", new BigDecimal("10.00"), 2);
        service.placeSellLimitOrder("AAA", "SELLER", new BigDecimal("8.00"), 2);

        ArrayList<Transaction> transactions = service.process("AAA");

        assertAll(
            () -> assertEquals(1, transactions.size()),
            () -> assertEquals(new BigDecimal("80.00"), buyer.getCash().getAmount()),
            () -> assertEquals(new BigDecimal("4.00"), buyer.getReservedCash().getAmount()),
            () -> assertEquals(2, buyer.getPositionsByTickers().get("AAA").getTotalQuantity()),
            () -> assertEquals(new BigDecimal("16.00"), seller.getCash().getAmount()),
            () -> assertEquals(0, seller.getPositionsByTickers().size())
        );
    }

    @Test
    void shouldProcessOrdersAcrossAllBooks() {
        TradingService service = new TradingService();
        service.registerInstrument(new Instrument(new Share("AAA", "Acme", 10.0), "AAA", Currency.getInstance("USD")));

        Portfolio buyer = new Portfolio("BUYER", Money.from(new BigDecimal("100.00"), Currency.getInstance("USD")));
        Portfolio seller = new Portfolio("SELLER", Money.from(BigDecimal.ZERO, Currency.getInstance("USD")));
        Position position = new Position("AAA", AssetType.SHARE);
        position.addLot(new Lot(new Date(), new BigDecimal("5.00"), 1));
        seller.addPosition(position);

        service.registerPortfolio("BUYER", buyer);
        service.registerPortfolio("SELLER", seller);

        service.placeBuyLimitOrder("AAA", "BUYER", new BigDecimal("10.00"), 1);
        service.placeSellLimitOrder("AAA", "SELLER", new BigDecimal("8.00"), 1);

        ArrayList<Transaction> transactions = service.processAll();

        assertEquals(1, transactions.size());
    }
}
