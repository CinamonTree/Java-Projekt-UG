package com.stockmarket;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Currency;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.jupiter.api.Test;

class TestPortfolioStorage {

    @Test
    void shouldSavePortfolioToFile() throws IOException {
        Portfolio portfolio = new Portfolio("P1", Money.from(new BigDecimal("100.00"), Currency.getInstance("USD")));
        portfolio.reserveCash(Money.from(new BigDecimal("30.00"), Currency.getInstance("USD")));

        Position position = new Position("OIL", AssetType.COMMODITY);
        position.addLot(new Lot(new GregorianCalendar(2025, Calendar.JANUARY, 5).getTime(), new BigDecimal("10.50"), 2));
        position.addLot(new Lot(new GregorianCalendar(2025, Calendar.JANUARY, 6).getTime(), new BigDecimal("11.00"), 3));
        portfolio.addPosition(position);

        Path filePath = Files.createTempFile("portfolio", ".txt");

        try {
            new PortfolioStorage().save(portfolio, filePath.toString());

            List<String> lines = Files.readAllLines(filePath);

            List<String> expected = List.of(
                "HEADER|PORTFOLIO|P1",
                "CASH|70.00|USD",
                "RESERVED|30.00|USD",
                "POSITION|COMMODITY|OIL|5",
                "LOT|2025-01-05|2|10.50",
                "LOT|2025-01-06|3|11.00"
            );

            assertEquals(expected, lines);
        } finally {
            Files.deleteIfExists(filePath);
        }
    }
}
