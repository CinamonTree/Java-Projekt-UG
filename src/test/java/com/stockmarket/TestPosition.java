package com.stockmarket;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Deque;

import org.junit.jupiter.api.Test;

import com.stockmarket.exceptions.InsufficientAssetsException;

class TestPosition {

    @Test
    void shouldIncreaseTotalQuantity() {
        Position position = new Position("AAA", AssetType.SHARE);

        position.addLot(new Lot(new Date(), new BigDecimal("5.00"), 2));
        position.addLot(new Lot(new Date(), new BigDecimal("7.00"), 3));

        assertEquals(5, position.getTotalQuantity());
    }

    @Test
    void shouldCalculateProfitUsingFifoLots() {
        Position position = new Position("AAA", AssetType.SHARE);
        position.addLot(new Lot(new Date(), new BigDecimal("5.00"), 10));
        position.addLot(new Lot(new Date(), new BigDecimal("8.00"), 5));

        BigDecimal profit = position.sell(new Date(), new BigDecimal("10.00"), 12);

        assertAll(
            () -> assertEquals(new BigDecimal("54.00"), profit),
            () -> assertEquals(3, position.getTotalQuantity())
        );
    }

    @Test
    void shouldRejectNonPositiveQuantity() {
        Position position = new Position("AAA", AssetType.SHARE);

        assertThrows(IllegalArgumentException.class, () -> position.sell(new Date(), new BigDecimal("10.00"), 0));
    }

    @Test
    void shouldThrowWhenLotsAreInsufficient() {
        Position position = new Position("AAA", AssetType.SHARE);
        position.addLot(new Lot(new Date(), new BigDecimal("5.00"), 2));

        assertThrows(InsufficientAssetsException.class, () -> position.sell(new Date(), new BigDecimal("10.00"), 3));
    }

    @Test
    void shouldReturnTrueWhenNoLots() {
        Position position = new Position("AAA", AssetType.SHARE);

        assertTrue(position.isEmpty());
    }

    @Test
    void shouldReturnFalseWhenLotsExist() {
        Position position = new Position("AAA", AssetType.SHARE);
        position.addLot(new Lot(new Date(), new BigDecimal("5.00"), 1));

        assertFalse(position.isEmpty());
    }

    @Test
    void shouldReturnDefensiveCopy() {
        Position position = new Position("AAA", AssetType.SHARE);
        Date date = parseDate("2025-01-01");
        position.addLot(new Lot(date, new BigDecimal("5.00"), 1));

        Deque<Lot> snapshot = position.getLotsSnapshot();
        snapshot.clear();

        assertEquals(1, position.getLotsSnapshot().size());
    }

    private Date parseDate(String value) {
    try {
        return new SimpleDateFormat("yyyy-MM-dd").parse(value);
    } catch (Exception ex) {
        throw new IllegalStateException(ex);
    }
    }
}
