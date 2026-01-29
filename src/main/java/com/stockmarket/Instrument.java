package com.stockmarket;

import java.util.Currency;

public class Instrument {
    
    private Asset asset;
    private String ticker;
    private Currency tradingCurrency;

    public Instrument(Asset asset, String ticker, Currency tradingCurrency) {
        this.asset = asset;
        this.ticker = ticker;
        this.tradingCurrency = tradingCurrency;
    }

    public Asset getAsset() {
        return asset;
    }

    public String getTicker() {
        return ticker;
    }

    public Currency getTradingCurrency() {
        return tradingCurrency;
    }

}
