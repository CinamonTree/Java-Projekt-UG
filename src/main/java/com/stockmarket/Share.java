package com.stockmarket;

public class Share extends Asset {

    public Share(String symbol, String name, double initialPrice) {
        super(AssetType.SHARE, symbol, name, initialPrice);
    }

    @Override
    public double calculateRealValue(int quantity) {
        return getInitialPrice() * quantity;
    }

    @Override
    public double calculatePurchaseCost(int quantity) {
        return getInitialPrice() * quantity;
    }
}
