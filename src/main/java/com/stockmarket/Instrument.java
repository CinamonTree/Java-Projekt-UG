package com.stockmarket;

import java.util.Currency;

public class Instrument {
    
    private Asset asset;
    private String code;
    private Currency tradingCurrency;

    public Asset getAsset() {
        return asset;
    }

    public String getCode() {
        return code;
    }

    public Currency getTradingCurrency() {
        return tradingCurrency;
    }

}
