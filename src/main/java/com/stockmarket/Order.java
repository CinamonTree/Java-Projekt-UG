package com.stockmarket;

import java.math.BigDecimal;

enum OrderType {
    MARKET, LIMIT
}

enum OrderSide {
    BUY, SELL
}

public class Order {

    private String orderID;
    private String portfolioID;
    private OrderSide orderSide;
    private OrderType orderType;
    private int quantity;
    private BigDecimal limitPrice;
    private int sequence;

    public Order(String orderID, String portfolioID, OrderSide orderSide, OrderType orderType, int quantity, BigDecimal limitPrice, int sequence) {
        this.orderID = orderID;
        this.portfolioID = portfolioID;
        this.orderSide = orderSide;
        this.orderType = orderType;
        this.quantity = quantity;
        this.limitPrice = limitPrice;
        this.sequence = sequence;
    } 

    public String getOrderID() {
        return orderID;
    }

    public String getPortfolioID() {
        return portfolioID;
    }

    public OrderSide getOrderSide() {
        return orderSide;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getLimitPrice() {
        return limitPrice;
    }

    public int getSequence() {
        return sequence;
    }

    public void reduceQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Ilość do zredukowania zlecenia musi być dodatnia! Przekazano:" + amount  + ".");
        }
        if (amount > quantity) {
            throw new IllegalArgumentException("Ilość do zredukowania zlecenia przekracza faktyczną ilość! Przekazano:" + amount + ". Dostępne:" + quantity + ".");
        }
        quantity -= amount;
    }

}
