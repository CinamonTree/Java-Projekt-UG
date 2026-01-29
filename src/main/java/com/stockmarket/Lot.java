package com.stockmarket;

import java.math.BigDecimal;
import java.util.Date;

public class Lot {

    private Date transactionDate;
    private BigDecimal unitPrice;
    private int quantity;

    public Lot(Date transactionDate, BigDecimal unitPrice, int quantity) {
        this.transactionDate = transactionDate;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }


    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void reduceQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        if (amount > quantity) {
            throw new IllegalArgumentException("amount exceeds lot quantity");
        }
        quantity -= amount;
    }
    
}
