package com.stockmarket;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;

import org.junit.jupiter.api.Test;

import com.stockmarket.exceptions.InsufficientAssetsException;
import com.stockmarket.exceptions.InsufficientFundsException;
import com.stockmarket.exceptions.PositionNotInPortfolioException;

class TestPortfolio {

    @Test
    void shouldApplyPurchaseForBuyer() {
        Portfolio portfolio = new Portfolio("P1", Money.from(new BigDecimal("100.00"), Currency.getInstance("USD")));
        Transaction transaction = new Transaction(new Date(), "AAA", AssetType.SHARE, "B-1", "S-1", "P1", "P2", 2, new BigDecimal("10.00"));

        BigDecimal profit = portfolio.applyTransaction(transaction);

        assertAll(
            () -> assertEquals(BigDecimal.ZERO, profit),
            () -> assertEquals(new BigDecimal("80.00"), portfolio.getCash().getAmount()),
            () -> assertEquals(2, portfolio.getPositionsByTickers().get("AAA").getTotalQuantity())
        );
    }

    @Test
    void shouldUseReservedCashForBuyerWhenAvailable() {
        Portfolio portfolio = new Portfolio("P1", Money.from(new BigDecimal("100.00"), Currency.getInstance("USD")));
        portfolio.reserveCash(Money.from(new BigDecimal("20.00"), Currency.getInstance("USD")));
        Transaction transaction = new Transaction(new Date(), "AAA", AssetType.SHARE, "B-1", "S-1", "P1", "P2", 2, new BigDecimal("10.00"));

        portfolio.applyTransaction(transaction);

        assertEquals(new BigDecimal("0.00"), portfolio.getReservedCash().getAmount());
    }

    @Test
    void shouldApplySaleForSeller() {
        Portfolio portfolio = new Portfolio("P1", Money.from(BigDecimal.ZERO, Currency.getInstance("USD")));
        Position position = new Position("AAA", AssetType.SHARE);
        position.addLot(new Lot(new Date(), new BigDecimal("5.00"), 2));
        portfolio.addPosition(position);
        Transaction transaction = new Transaction(new Date(), "AAA", AssetType.SHARE, "B-1", "S-1", "P2", "P1", 2, new BigDecimal("10.00"));

        BigDecimal profit = portfolio.applyTransaction(transaction);

        assertAll(
            () -> assertEquals(new BigDecimal("10.00"), profit),
            () -> assertEquals(new BigDecimal("20.00"), portfolio.getCash().getAmount()),
            () -> assertTrue(portfolio.getPositionsByTickers().isEmpty())
        );
    }

    @Test
    void shouldReturnZeroForUnrelatedTransaction() {
        Portfolio portfolio = new Portfolio("P1", Money.from(new BigDecimal("100.00"), Currency.getInstance("USD")));
        Transaction transaction = new Transaction(new Date(), "AAA", AssetType.SHARE, "B-1", "S-1", "P2", "P3", 2, new BigDecimal("10.00"));

        BigDecimal profit = portfolio.applyTransaction(transaction);

        assertEquals(BigDecimal.ZERO, profit);
    }

    @Test
    void shouldThrowWhenSellingMissingPosition() {
        Portfolio portfolio = new Portfolio("P1", Money.from(new BigDecimal("100.00"), Currency.getInstance("USD")));
        Transaction transaction = new Transaction(new Date(), "AAA", AssetType.SHARE, "B-1", "S-1", "P2", "P1", 2, new BigDecimal("10.00"));

        assertThrows(PositionNotInPortfolioException.class, () -> portfolio.applyTransaction(transaction));
    }

    @Test
    void shouldMoveCashToReserved() {
        Portfolio portfolio = new Portfolio("P1", Money.from(new BigDecimal("100.00"), Currency.getInstance("USD")));

        portfolio.reserveCash(Money.from(new BigDecimal("30.00"), Currency.getInstance("USD")));

        assertAll(
            () -> assertEquals(new BigDecimal("70.00"), portfolio.getCash().getAmount()),
            () -> assertEquals(new BigDecimal("30.00"), portfolio.getReservedCash().getAmount())
        );
    }

    @Test
    void shouldRejectReservationWhenInsufficient() {
        Portfolio portfolio = new Portfolio("P1", Money.from(new BigDecimal("10.00"), Currency.getInstance("USD")));

        assertThrows(InsufficientFundsException.class,
            () -> portfolio.reserveCash(Money.from(new BigDecimal("30.00"), Currency.getInstance("USD"))));
    }

    @Test
    void shouldMoveReservedToCash() {
        Portfolio portfolio = new Portfolio("P1", Money.from(new BigDecimal("100.00"), Currency.getInstance("USD")));
        portfolio.reserveCash(Money.from(new BigDecimal("30.00"), Currency.getInstance("USD")));

        portfolio.releaseReservedCash(Money.from(new BigDecimal("10.00"), Currency.getInstance("USD")));

        assertAll(
            () -> assertEquals(new BigDecimal("80.00"), portfolio.getCash().getAmount()),
            () -> assertEquals(new BigDecimal("20.00"), portfolio.getReservedCash().getAmount())
        );
    }

    @Test
    void shouldRejectReleaseWhenInsufficient() {
        Portfolio portfolio = new Portfolio("P1", Money.from(new BigDecimal("10.00"), Currency.getInstance("USD")));

        assertThrows(InsufficientFundsException.class,
            () -> portfolio.releaseReservedCash(Money.from(new BigDecimal("1.00"), Currency.getInstance("USD"))));
    }

    @Test
    void shouldIncreaseCashBalance() {
        Portfolio portfolio = new Portfolio("P1", Money.from(new BigDecimal("10.00"), Currency.getInstance("USD")));

        portfolio.depositCash(Money.from(new BigDecimal("5.00"), Currency.getInstance("USD")));

        assertEquals(new BigDecimal("15.00"), portfolio.getCash().getAmount());
    }

    @Test
    void shouldDecreaseCashBalance() {
        Portfolio portfolio = new Portfolio("P1", Money.from(new BigDecimal("10.00"), Currency.getInstance("USD")));

        portfolio.withdrawCash(Money.from(new BigDecimal("5.00"), Currency.getInstance("USD")));

        assertEquals(new BigDecimal("5.00"), portfolio.getCash().getAmount());
    }

    @Test
    void shouldRejectWithdrawalWhenInsufficient() {
        Portfolio portfolio = new Portfolio("P1", Money.from(new BigDecimal("10.00"), Currency.getInstance("USD")));

        assertThrows(InsufficientFundsException.class,
            () -> portfolio.withdrawCash(Money.from(new BigDecimal("50.00"), Currency.getInstance("USD"))));
    }

    @Test
    void shouldManageWatchlistEntries() {
        Portfolio portfolio = new Portfolio("P1", Money.from(new BigDecimal("10.00"), Currency.getInstance("USD")));

        portfolio.addToWatchlist("AAA");
        portfolio.removeFromWatchlist("AAA");

        assertTrue(portfolio.getWatchlist().isEmpty());
    }

    @Test
    void shouldReturnDefensiveCopyOfWatchlist() {
        Portfolio portfolio = new Portfolio("P1", Money.from(new BigDecimal("10.00"), Currency.getInstance("USD")));
        portfolio.addToWatchlist("AAA");

        HashSet<String> watchlist = portfolio.getWatchlist();
        watchlist.clear();

        assertEquals(1, portfolio.getWatchlist().size());
    }

    @Test
    void shouldThrowWhenPositionMissingOrInsufficient() {
        Portfolio portfolio = new Portfolio("P1", Money.from(new BigDecimal("10.00"), Currency.getInstance("USD")));
        Position position = new Position("AAA", AssetType.SHARE);
        position.addLot(new Lot(new Date(), new BigDecimal("5.00"), 1));
        portfolio.addPosition(position);

        assertThrows(InsufficientAssetsException.class, () -> portfolio.ensureHasPosition("AAA", 2));
    }

    @Test
    void shouldAllowEnsuringPositionWhenSufficient() {
        Portfolio portfolio = new Portfolio("P1", Money.from(new BigDecimal("10.00"), Currency.getInstance("USD")));
        Position position = new Position("AAA", AssetType.SHARE);
        position.addLot(new Lot(new Date(), new BigDecimal("5.00"), 2));
        portfolio.addPosition(position);

        assertDoesNotThrow(() -> portfolio.ensureHasPosition("AAA", 2));
    }
}
