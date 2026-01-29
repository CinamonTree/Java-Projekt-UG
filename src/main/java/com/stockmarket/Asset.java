package com.stockmarket;

import java.util.Objects;

public abstract class Asset {

    private final AssetType type;
    private final String ID;
    private final String name;
    private final double initialPrice;

    protected Asset(AssetType type, String symbol, String name, double initialPrice) {
        this.type = type;
        this.ID = symbol;
        this.name = name;
        this.initialPrice = initialPrice;
    }

    public AssetType getType() {
        return type;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public double getInitialPrice() {
        return initialPrice;
    }

    public abstract double calculateRealValue(int quantity);

    public abstract double calculatePurchaseCost(int quantity);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Asset)) return false;
        Asset asset = (Asset) o;
        return type == asset.type && Objects.equals(ID, asset.ID);
    }

    @Override
    public int hashCode() {
        return 31 * type.hashCode() + ID.hashCode();
    }
}
