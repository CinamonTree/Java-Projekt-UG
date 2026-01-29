package com.stockmarket;

import java.math.BigDecimal;
import java.util.Currency;

public class Money {
    
    private Currency currency;
    private BigDecimal amount;

    private Money(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public static Money fromString(String amount, String code) {
        Currency currency = Currency.getInstance(code);
        return new Money(new BigDecimal(amount), currency);
    }

    public static Money from(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public Money add(Money money) {
        money = validateIsSameCurrency(money);
        return new Money(this.amount.add(money.amount), money.currency);
    }

    public Money subtract(Money money) {
        money = validateIsSameCurrency(money);
        return new Money(this.amount.subtract(money.amount), money.currency);
    }

    public Money multiply(BigDecimal multiplier) {
        return new Money(this.amount.multiply(multiplier), currency);
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public int compareTo(Money money) {
        money = validateIsSameCurrency(money);
        return this.amount.compareTo(money.amount);
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
