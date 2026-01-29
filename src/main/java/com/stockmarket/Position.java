package com.stockmarket;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;

import com.stockmarket.exceptions.InsufficientAssetsException;

/** Reprezentuje otwartą pozycję portfela. */
public class Position {
    
    private String ticker;
    private Deque<Lot> lots;
    private AssetType assetType;

    public Position(String ticker, AssetType assetType) {
        this.ticker = ticker;
        this.assetType = assetType;
        this.lots = new ArrayDeque<>();
    }

    public String getTicker() {
        return ticker;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public void addLot(Lot lot) {
        lots.addLast(lot);
    }

    public int getTotalQuantity() {
        int total = 0;
        for (Lot lot : lots) {
            total += lot.getQuantity();
        }
        return total;
    }

    public BigDecimal sell(Date transactionDate, BigDecimal unitPrice, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        int remaining = quantity;
        BigDecimal profit = BigDecimal.ZERO;

        while (remaining > 0 && !lots.isEmpty()) {
            Lot lot = lots.peekFirst();
            int available = lot.getQuantity();
            int consumed = Math.min(available, remaining);

            BigDecimal lotProfit = unitPrice.subtract(lot.getUnitPrice()).multiply(BigDecimal.valueOf(consumed));
            profit = profit.add(lotProfit);

            lot.reduceQuantity(consumed);
            remaining -= consumed;

            if (lot.getQuantity() == 0) {
                lots.removeFirst();
            }
        }

        if (remaining > 0) {
            throw new InsufficientAssetsException("Not enough quantity to sell. Missing: " + remaining);
        }

        return profit;
    }

    public boolean isEmpty() {
        return lots.isEmpty();
    }

    public Deque<Lot> getLotsSnapshot() {
        return new ArrayDeque<>(lots);
    }

}
