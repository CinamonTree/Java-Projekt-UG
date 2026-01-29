package com.stockmarket;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class PortfolioStorage {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    public void save(Portfolio portfolio, String filePath) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(filePath));
            writer.write("HEADER|PORTFOLIO|" + portfolio.getPortfolioID());
            writer.newLine();
            writer.write("CASH|" + portfolio.getCash().getAmount() + "|" + portfolio.getCash().getCurrency().getCurrencyCode());
            writer.newLine();
            writer.write("RESERVED|" + portfolio.getReservedCash().getAmount() + "|" + portfolio.getReservedCash().getCurrency().getCurrencyCode());
            writer.newLine();

            HashMap<String, Position> positions = portfolio.getPositionsByTickers();
            for (Map.Entry<String, Position> entry : positions.entrySet()) {
                Position position = entry.getValue();
                int totalQuantity = position.getTotalQuantity();
                writer.write("POSITION|" + position.getAssetType() + "|" + position.getTicker() + "|" + totalQuantity);
                writer.newLine();
                writeLots(writer, position);
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private void writeLots(BufferedWriter writer, Position position) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        for (Lot lot : position.getLotsSnapshot()) {
            String date = dateFormat.format(lot.getTransactionDate());
            writer.write("LOT|" + date + "|" + lot.getQuantity() + "|" + lot.getUnitPrice());
            writer.newLine();
        }
    }

}
