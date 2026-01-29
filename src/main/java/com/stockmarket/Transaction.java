package com.stockmarket;

import java.math.BigDecimal;
import java.util.Date;

/** Klasa reprezentująca udane transakcje. Po przekazaniu do portfolio tworzy odpowiednie partie danego instrumentu. */
public class Transaction {

    private final Date date;
    private final String ticker;
    private final AssetType assetType;
    private final String buyOrderID;
    private final String sellOrderID;
    private final String buyerPortfolioID;
    private final String sellerPortfolioID;
    private final int quantity;
    private final BigDecimal unitPrice;

    public Transaction(Date date, String ticker, AssetType assetType, String buyOrderID, String sellOrderID,
                       String buyerPortfolioID, String sellerPortfolioID, int quantity, BigDecimal unitPrice) {
        this.date = date;
        this.ticker = ticker;
        this.assetType = assetType;
        this.buyOrderID = buyOrderID;
        this.sellOrderID = sellOrderID;
        this.buyerPortfolioID = buyerPortfolioID;
        this.sellerPortfolioID = sellerPortfolioID;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Date getDate() {
        return date;
    }

    public String getTicker() {
        return ticker;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public String getBuyOrderID() {
        return buyOrderID;
    }

    public String getSellOrderID() {
        return sellOrderID;
    }

    public String getBuyerPortfolioID() {
        return buyerPortfolioID;
    }

    public String getSellerPortfolioID() {
        return sellerPortfolioID;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
}
