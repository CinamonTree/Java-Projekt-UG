package com.stockmarket;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Currency;

import org.junit.jupiter.api.Test;

class TestInstrument {

    @Test
    void shouldReturnConstructorValues() {
        Share share = new Share("AAA", "Acme", 10.0);
        Currency currency = Currency.getInstance("USD");
        Instrument instrument = new Instrument(share, "ACM", currency);

        assertAll(
            () -> assertEquals(share, instrument.getAsset()),
            () -> assertEquals("ACM", instrument.getTicker()),
            () -> assertEquals(currency, instrument.getTradingCurrency())
        );
    }
}
