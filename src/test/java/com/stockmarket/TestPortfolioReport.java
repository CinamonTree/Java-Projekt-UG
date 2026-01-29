package com.stockmarket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.junit.jupiter.api.Test;

class TestPortfolioReport {

    @Test
    void shouldGenerateReportSortedByTypeAndValue() {
        Portfolio portfolio = new Portfolio("P1", Money.from(new BigDecimal("100.00"), Currency.getInstance("USD")));

        Position shareLow = new Position("AAA", AssetType.SHARE);
        shareLow.addLot(new Lot(new Date(), new BigDecimal("10.00"), 2));
        portfolio.addPosition(shareLow);

        Position shareHigh = new Position("BBB", AssetType.SHARE);
        shareHigh.addLot(new Lot(new Date(), new BigDecimal("50.00"), 1));
        portfolio.addPosition(shareHigh);

        Position commodity = new Position("OIL", AssetType.COMMODITY);
        commodity.addLot(new Lot(new Date(), new BigDecimal("2.00"), 4));
        portfolio.addPosition(commodity);

        Position currency = new Position("EUR", AssetType.CURRENCY);
        currency.addLot(new Lot(new Date(), new BigDecimal("10.00"), 3));
        portfolio.addPosition(currency);

        HashMap<String, Instrument> instruments = new HashMap<>();
        instruments.put("AAA", new Instrument(new Share("AAA", "Acme", 10.0), "AAA", Currency.getInstance("USD")));
        instruments.put("BBB", new Instrument(new Share("BBB", "Beta", 50.0), "BBB", Currency.getInstance("USD")));
        instruments.put("OIL", new Instrument(new CommodityAsset("OIL", "Oil", 2.0), "OIL", Currency.getInstance("USD")));
        instruments.put("EUR", new Instrument(new com.stockmarket.Currency("EUR", "Euro", 10.0, 0.1), "EUR", Currency.getInstance("USD")));

        PortfolioReport report = new PortfolioReport();
        String result = report.generate(portfolio, instruments);

        String expected = ""
            + "PORTFOLIO|P1\n"
            + "ASSET|SHARE|BBB|1|" + format(50.0) + "\n"
            + "ASSET|SHARE|AAA|2|" + format(20.0) + "\n"
            + "ASSET|COMMODITY|OIL|4|" + format(8.0) + "\n"
            + "ASSET|CURRENCY|EUR|3|" + format(27.0) + "\n";

        assertEquals(expected, result);
    }

    @Test
    void shouldThrowWhenInstrumentMissing() {
        Portfolio portfolio = new Portfolio("P1", Money.from(new BigDecimal("100.00"), Currency.getInstance("USD")));
        Position position = new Position("AAA", AssetType.SHARE);
        position.addLot(new Lot(new Date(), new BigDecimal("10.00"), 2));
        portfolio.addPosition(position);

        PortfolioReport report = new PortfolioReport();

        assertThrows(IllegalArgumentException.class, () -> report.generate(portfolio, new HashMap<>()));
    }

    private String format(double value) {
        return String.format(Locale.getDefault(), "%.2f", value);
    }
}
