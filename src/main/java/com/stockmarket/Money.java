package com.stockmarket.domain;

import java.math.BigDecimal;
import java.util.Currency;


/** Money reprezentuje ilości pieniężne w danej walucie i zapewnia bezpieczne operacje na nich. */
public class Money {
    
    private Currency currency;
    private BigDecimal amount;

    private Money(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    /** Zwraca instancję money z reprezentacji znakowej liczby i waluty. np. 10.5 usd. */
    public static Money fromString(String amount, String code) {
        Currency currency = Currency.getInstance(code);
        return new Money(new BigDecimal(amount), currency);
    }

    /** Sumuje dwie ilości pieniężne. */
    public Money add(Money money) {
        money = validateIsSameCurrency(money);
        return new Money(this.amount.add(money.amount), money.currency);
    }

    /** Odejmuje dwie ilości pieniężne. */
    public Money subtract(Money money) {
        money = validateIsSameCurrency(money);
        return new Money(this.amount.subtract(money.amount), money.currency);
    }

    private Money validateIsSameCurrency(Money money) {
        if (!this.currency.equals(money.currency)) {
            throw new IllegalArgumentException("Nie można operować na pieniądzach w różnych walutach! Podano:" + this.currency.getSymbol() + " != " + money.currency.getSymbol());
        }
        return money;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Money money = (Money) o;

        return this.currency.equals(money.currency) && (this.amount.compareTo(money.amount) == 0);
    }

    @Override
    public int hashCode() {
        return 31 * currency.hashCode() + amount.stripTrailingZeros().hashCode();
    }

    @Override
    public String toString() {
        return amount + " " + currency.getCurrencyCode();
    }

}