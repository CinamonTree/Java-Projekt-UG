package com.stockmarket;

/** Asset representing currencies with spread applied. */
public class Currency extends Asset {

    private final double spreadPercent;

    /** Creates a currency asset with a spread percentage. */
    public Currency(String symbol, String name, double initialPrice, double spreadPercent) {
        super(AssetType.CURRENCY, symbol, name, initialPrice);
        this.spreadPercent = spreadPercent;
    }

    /** Returns the spread percentage. */
    public double getSpreadPercent() {
        return spreadPercent;
    }

    @Override
    /** Calculates market value after spread. */
    public double calculateRealValue(int quantity) {
        return getInitialPrice() * quantity * (1.0 - spreadPercent);
    }

    @Override
    /** Calculates purchase cost including spread. */
    public double calculatePurchaseCost(int quantity) {
        return getInitialPrice() * quantity * (1.0 + spreadPercent);
    }
}
