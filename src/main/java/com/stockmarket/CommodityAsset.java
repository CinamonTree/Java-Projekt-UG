package com.stockmarket;

/** Asset representing commodities with direct price usage. */
public class CommodityAsset extends Asset {

    /** Creates a commodity asset instance. */
    public CommodityAsset(String symbol, String name, double initialPrice) {
        super(AssetType.COMMODITY, symbol, name, initialPrice);
    }

    @Override
    /** Calculates market value for commodities. */
    public double calculateRealValue(int quantity) {
        return getInitialPrice() * quantity;
    }

    @Override
    /** Calculates purchase cost for commodities. */
    public double calculatePurchaseCost(int quantity) {
        return getInitialPrice() * quantity;
    }
}
