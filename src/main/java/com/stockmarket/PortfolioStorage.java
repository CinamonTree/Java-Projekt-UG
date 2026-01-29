package com.stockmarket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.stockmarket.exceptions.DataIntegrityException;

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

    public Portfolio load(String filePath) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line;
            Portfolio portfolio = null;
            Position currentPosition = null;
            int expectedPositionQuantity = 0;
            int accumulatedLotQuantity = 0;
            Money cash = null;
            Money reserved = null;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\|");
                if (parts.length == 0) {
                    continue;
                }
                String recordType = parts[0];

                if ("HEADER".equals(recordType)) {
                    if (parts.length < 3) {
                        throw new DataIntegrityException("Invalid HEADER record.");
                    }
                    String portfolioId = parts[2];
                    portfolio = new Portfolio(portfolioId, Money.of(BigDecimal.ZERO, Currency.getInstance("USD")));
                } else if ("CASH".equals(recordType)) {
                    ensurePortfolioInitialized(portfolio);
                    if (parts.length < 3) {
                        throw new DataIntegrityException("Invalid CASH record.");
                    }
                    cash = Money.of(new BigDecimal(parts[1]), Currency.getInstance(parts[2]));
                } else if ("RESERVED".equals(recordType)) {
                    ensurePortfolioInitialized(portfolio);
                    if (parts.length < 3) {
                        throw new DataIntegrityException("Invalid RESERVED record.");
                    }
                    reserved = Money.of(new BigDecimal(parts[1]), Currency.getInstance(parts[2]));
                } else if ("POSITION".equals(recordType)) {
                    ensurePortfolioInitialized(portfolio);
                    if (currentPosition != null) {
                        validatePositionQuantity(currentPosition, expectedPositionQuantity, accumulatedLotQuantity);
                    }
                    if (parts.length < 4) {
                        throw new DataIntegrityException("Invalid POSITION record.");
                    }
                    AssetType assetType = AssetType.valueOf(parts[1]);
                    String ticker = parts[2];
                    expectedPositionQuantity = Integer.parseInt(parts[3]);
                    accumulatedLotQuantity = 0;
                    currentPosition = new Position(ticker, assetType);
                    portfolio.addPosition(currentPosition);
                } else if ("LOT".equals(recordType)) {
                    ensurePortfolioInitialized(portfolio);
                    if (currentPosition == null) {
                        throw new DataIntegrityException("LOT without POSITION.");
                    }
                    if (parts.length < 4) {
                        throw new DataIntegrityException("Invalid LOT record.");
                    }
                    Date date = parseDate(parts[1]);
                    int quantity = Integer.parseInt(parts[2]);
                    BigDecimal unitPrice = new BigDecimal(parts[3]);
                    currentPosition.addLot(new Lot(date, unitPrice, quantity));
                    accumulatedLotQuantity += quantity;
                } else {
                    throw new DataIntegrityException("Unknown record type: " + recordType);
                }
            }

            if (currentPosition != null) {
                validatePositionQuantity(currentPosition, expectedPositionQuantity, accumulatedLotQuantity);
            }

            if (portfolio == null) {
                throw new DataIntegrityException("Missing HEADER record.");
            }

            if (cash == null || reserved == null) {
                throw new DataIntegrityException("Missing CASH or RESERVED record.");
            }

            if (!cash.getCurrency().equals(reserved.getCurrency())) {
                throw new DataIntegrityException("Currency mismatch between CASH and RESERVED.");
            }

            portfolio.setBalances(cash, reserved);

            return portfolio;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private void ensurePortfolioInitialized(Portfolio portfolio) {
        if (portfolio == null) {
            throw new DataIntegrityException("Missing HEADER record before data.");
        }
    }

    private void validatePositionQuantity(Position position, int expected, int actual) {
        if (expected != actual) {
            throw new DataIntegrityException("Position quantity mismatch for " + position.getTicker() + ". Expected: " + expected + ", actual: " + actual);
        }
    }

    private Date parseDate(String value) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return dateFormat.parse(value);
        } catch (ParseException e) {
            throw new DataIntegrityException("Invalid date format: " + value);
        }
    }
}
