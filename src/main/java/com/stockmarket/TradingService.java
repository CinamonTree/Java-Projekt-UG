package com.stockmarket;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TradingService {

    private final Map<String, OrderBook> orderBooksByTicker = new HashMap<>();
    private final Map<String, Portfolio> portfoliosById = new HashMap<>();
    private final Map<String, Instrument> instrumentsByTicker = new HashMap<>();

    public void registerInstrument(Instrument instrument) {
        instrumentsByTicker.put(instrument.getTicker(), instrument);
        if (!orderBooksByTicker.containsKey(instrument.getTicker())) {
            orderBooksByTicker.put(instrument.getTicker(), new OrderBook(instrument.getTicker(), instrument.getAsset().getType()));
        }
    }

    public void registerPortfolio(String portfolioId, Portfolio portfolio) {
        portfoliosById.put(portfolioId, portfolio);
    }

    public void placeSellMarketOrder(String ticker, String portfolioId, int quantity) {
        OrderBook orderBook = getOrderBook(ticker);
        Portfolio portfolio = ensurePortfolioExists(portfolioId);
        portfolio.ensureHasPosition(ticker, quantity);
        orderBook.placeSellMarketOrder(portfolioId, quantity);
    }

    public void placeBuyMarketOrder(String ticker, String portfolioId, int quantity) {
        OrderBook orderBook = getOrderBook(ticker);
        ensurePortfolioExists(portfolioId);
        orderBook.placeBuyMarketOrder(portfolioId, quantity);
    }

    public void placeSellLimitOrder(String ticker, String portfolioId, BigDecimal priceLimit, int quantity) {
        OrderBook orderBook = getOrderBook(ticker);
        Portfolio portfolio = ensurePortfolioExists(portfolioId);
        portfolio.ensureHasPosition(ticker, quantity);
        orderBook.placeSellLimitOrder(portfolioId, priceLimit, quantity);
    }

    public void placeBuyLimitOrder(String ticker, String portfolioId, BigDecimal priceLimit, int quantity) {
        OrderBook orderBook = getOrderBook(ticker);
        Portfolio portfolio = ensurePortfolioExists(portfolioId);
        Money cost = Money.from(priceLimit.multiply(BigDecimal.valueOf(quantity)), portfolio.getCash().getCurrency());
        portfolio.reserveCash(cost);
        orderBook.placeBuyLimitOrder(portfolioId, priceLimit, quantity);
    }

    public ArrayList<Transaction> process(String ticker) {
        OrderBook orderBook = getOrderBook(ticker);
        ArrayList<Transaction> transactions = orderBook.process();
        applyTransactions(transactions);
        return transactions;
    }

    public ArrayList<Transaction> processAll() {
        ArrayList<Transaction> transactions = new ArrayList<>();
        for (OrderBook orderBook : orderBooksByTicker.values()) {
            ArrayList<Transaction> processed = orderBook.process();
            for (Transaction transaction : processed) {
                transactions.add(transaction);
            }
        }
        applyTransactions(transactions);
        return transactions;
    }

    private OrderBook getOrderBook(String ticker) {
        OrderBook orderBook = orderBooksByTicker.get(ticker);
        if (orderBook == null) {
            throw new IllegalArgumentException("No order book for ticker: " + ticker);
        }
        return orderBook;
    }

    private Portfolio ensurePortfolioExists(String portfolioId) {
        Portfolio portfolio = portfoliosById.get(portfolioId);
        if (portfolio == null) {
            throw new IllegalArgumentException("No portfolio with id: " + portfolioId);
        }
        return portfolio;
    }

    private void applyTransactions(ArrayList<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            Portfolio buyer = portfoliosById.get(transaction.getBuyerPortfolioID());
            if (buyer != null) {
                buyer.applyTransaction(transaction);
            }
            Portfolio seller = portfoliosById.get(transaction.getSellerPortfolioID());
            if (seller != null && seller != buyer) {
                seller.applyTransaction(transaction);
            }
        }
    }
}
