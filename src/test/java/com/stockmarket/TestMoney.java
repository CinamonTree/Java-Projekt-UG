package com.stockmarket;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class TestMoney {

    @Test
    void shouldCreateMoneyFromString() {
        Money money = Money.fromString("10.50", "USD");

        assertAll(
            () -> assertEquals(new BigDecimal("10.50"), money.getAmount()),
            () -> assertEquals(Currency.getInstance("USD"), money.getCurrency())
        );
    }

    @Test
    void shouldCreateMoneyFromFactory() {
        Money money = Money.from(new BigDecimal("5.00"), Currency.getInstance("EUR"));

        assertAll(
            () -> assertEquals(new BigDecimal("5.00"), money.getAmount()),
            () -> assertEquals(Currency.getInstance("EUR"), money.getCurrency())
        );
    }

    @Test
    void shouldCreateMoneyFromOfFactory() {
        Money money = Money.from(new BigDecimal("7.25"), Currency.getInstance("USD"));

        assertAll(
            () -> assertEquals(new BigDecimal("7.25"), money.getAmount()),
            () -> assertEquals(Currency.getInstance("USD"), money.getCurrency())
        );
    }

    @Test
    void shouldAddSameCurrencyAmounts() {
        Money left = Money.from(new BigDecimal("10.00"), Currency.getInstance("USD"));
        Money right = Money.from(new BigDecimal("2.50"), Currency.getInstance("USD"));

        Money result = left.add(right);

        assertEquals(new BigDecimal("12.50"), result.getAmount());
    }

    @Test
    void shouldRejectDifferentCurrenciesOnAdd() {
        Money left = Money.from(new BigDecimal("10.00"), Currency.getInstance("USD"));
        Money right = Money.from(new BigDecimal("2.50"), Currency.getInstance("EUR"));

        assertThrows(IllegalArgumentException.class, () -> left.add(right));
    }

    @Test
    void shouldSubtractSameCurrencyAmounts() {
        Money left = Money.from(new BigDecimal("10.00"), Currency.getInstance("USD"));
        Money right = Money.from(new BigDecimal("2.50"), Currency.getInstance("USD"));

        Money result = left.subtract(right);

        assertEquals(new BigDecimal("7.50"), result.getAmount());
    }

    @Test
    void shouldRejectDifferentCurrenciesOnSubtract() {
        Money left = Money.from(new BigDecimal("10.00"), Currency.getInstance("USD"));
        Money right = Money.from(new BigDecimal("2.50"), Currency.getInstance("EUR"));

        assertThrows(IllegalArgumentException.class, () -> left.subtract(right));
    }

    @Test
    void shouldMultiplyAmount() {
        Money money = Money.from(new BigDecimal("10.00"), Currency.getInstance("USD"));

        Money result = money.multiply(new BigDecimal("1.5"));

        assertEquals(new BigDecimal("15.000"), result.getAmount());
    }

    @Test
    void shouldCompareSameCurrencyAmounts() {
        Money left = Money.from(new BigDecimal("10.00"), Currency.getInstance("USD"));
        Money right = Money.from(new BigDecimal("12.00"), Currency.getInstance("USD"));

        assertEquals(-1, left.compareTo(right));
    }

    @Test
    void shouldRejectDifferentCurrenciesOnCompareTo() {
        Money left = Money.from(new BigDecimal("10.00"), Currency.getInstance("USD"));
        Money right = Money.from(new BigDecimal("12.00"), Currency.getInstance("EUR"));

        assertThrows(IllegalArgumentException.class, () -> left.compareTo(right));
    }

    @Test
    void shouldTreatSameAmountWithDifferentScaleAsEqual() {
        Money left = Money.from(new BigDecimal("1.0"), Currency.getInstance("USD"));
        Money right = Money.from(new BigDecimal("1.00"), Currency.getInstance("USD"));

        assertAll(
            () -> assertEquals(left, right),
            () -> assertEquals(left.hashCode(), right.hashCode())
        );
    }

    @Test
    void shouldIncludeAmountAndCurrencyCode() {
        Money money = Money.from(new BigDecimal("10.50"), Currency.getInstance("USD"));

        assertEquals("10.50 USD", money.toString());
    }
}
