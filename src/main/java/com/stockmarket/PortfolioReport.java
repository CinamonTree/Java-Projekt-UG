package com.stockmarket;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class PortfolioReport {

    public String generate(Portfolio portfolio, Map<String, Instrument> instrumentsByTicker) {
        ArrayList<PositionValue> values = new ArrayList<>();
        HashMap<String, Position> positions = portfolio.getPositionsByTickers();
        for (Map.Entry<String, Position> entry : positions.entrySet()) {
            String ticker = entry.getKey();
            Position position = entry.getValue();
            Instrument instrument = instrumentsByTicker.get(ticker);
            if (instrument == null) {
                throw new IllegalArgumentException("Missing instrument for ticker: " + ticker);
            }
            int quantity = position.getTotalQuantity();
            double marketValue = instrument.getAsset().calculateRealValue(quantity);
            values.add(new PositionValue(position, quantity, marketValue));
        }

        values.sort(new Comparator<PositionValue>() {
            @Override
            public int compare(PositionValue left, PositionValue right) {
                int typeCompare = left.position.getAssetType().compareTo(right.position.getAssetType());
                if (typeCompare != 0) {
                    return typeCompare;
                }
                return Double.compare(right.marketValue, left.marketValue);
            }
        });

        DecimalFormat format = new DecimalFormat("0.00");
        StringBuilder report = new StringBuilder();
        report.append("PORTFOLIO|").append(portfolio.getPortfolioID()).append("\n");
        for (PositionValue value : values) {
            report.append("ASSET|")
                .append(value.position.getAssetType())
                .append("|")
                .append(value.position.getTicker())
                .append("|")
                .append(value.quantity)
                .append("|")
                .append(format.format(value.marketValue))
                .append("\n");
        }
        return report.toString();
    }

    private static class PositionValue {
        private final Position position;
        private final int quantity;
        private final double marketValue;

        private PositionValue(Position position, int quantity, double marketValue) {
            this.position = position;
            this.quantity = quantity;
            this.marketValue = marketValue;
        }
    }
}
