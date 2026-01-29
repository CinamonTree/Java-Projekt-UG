package com.stockmarket;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;

import com.stockmarket.exceptions.PositionNotInPortfolioException;
import com.stockmarket.exceptions.InsufficientAssetsException;
import com.stockmarket.exceptions.InsufficientFundsException;

public class Portfolio {
    
    private String portfolioID;
    private Money cash;
    private Money reservedCash;
    private HashMap<String, Position> positionsByTickers;
    private HashSet<String> watchlist;

    public Portfolio(String portfolioID, Money initialCash) {
        this.portfolioID = portfolioID;
        this.cash = initialCash;
        this.reservedCash = Money.from(BigDecimal.ZERO, initialCash.getCurrency());
        this.positionsByTickers = new HashMap<>();
        this.watchlist = new HashSet<>();
    }

    public String getPortfolioID() {
        return portfolioID;
    }

    public Money getCash() {
        return cash;
    }

    public Money getReservedCash() {
        return reservedCash;
    }

    public BigDecimal applyTransaction(Transaction transaction) {
        if (portfolioID.equals(transaction.getBuyerPortfolioID())) {
            Money cost = costForTransaction(transaction, cash.getCurrency());
            if (reservedCash.compareTo(cost) >= 0) {
                reservedCash = reservedCash.subtract(cost);
            } else {
                cash = cash.subtract(cost);
            }
            Position position = positionsByTickers.get(transaction.getTicker());
            if (position == null) {
                position = new Position(transaction.getTicker(), transaction.getAssetType());
                positionsByTickers.put(transaction.getTicker(), position);
            }
            position.addLot(new Lot(transaction.getDate(), transaction.getUnitPrice(), transaction.getQuantity()));
            return BigDecimal.ZERO;
        }

        if (portfolioID.equals(transaction.getSellerPortfolioID())) {
            Position position = positionsByTickers.get(transaction.getTicker());
            if (position == null) {
                throw new PositionNotInPortfolioException("Position for ticker not found: " + transaction.getTicker());
            }
            BigDecimal profit = position.sell(transaction.getDate(), transaction.getUnitPrice(), transaction.getQuantity());
            Money proceeds = costForTransaction(transaction, cash.getCurrency());
            cash = cash.add(proceeds);
            if (position.isEmpty()) {
                positionsByTickers.remove(transaction.getTicker());
            }
            return profit;
        }

        return BigDecimal.ZERO;
    }

    public void reserveCash(Money cashToReserve) {
        if (cash.compareTo(cashToReserve) < 0) {
            throw new InsufficientFundsException("Not enough cash to reserve. Available: " + cash + ", requested: " + cashToReserve);
        }
        cash = cash.subtract(cashToReserve);
        reservedCash = reservedCash.add(cashToReserve);
    }

    public void releaseReservedCash(Money cashToRelease) {
        if (reservedCash.compareTo(cashToRelease) < 0) {
            throw new InsufficientFundsException("Not enough reserved cash to release. Available: " + reservedCash + ", requested: " + cashToRelease);
        }
        reservedCash = reservedCash.subtract(cashToRelease);
        cash = cash.add(cashToRelease);
    }

    public void depositCash(Money cashToDeposit) {
        cash = cash.add(cashToDeposit);
    }

    public void withdrawCash(Money cashToWithdraw) {
        if (cash.compareTo(cashToWithdraw) < 0) {
            throw new InsufficientFundsException("Not enough cash to withdraw. Available: " + cash + ", requested: " + cashToWithdraw);
        }
        cash = cash.subtract(cashToWithdraw);
    }

    public void addToWatchlist(String ticker) {
        watchlist.add(ticker);
    }

    public void removeFromWatchlist(String ticker) {
        watchlist.remove(ticker);
    }

    public HashSet<String> getWatchlist() {
        return new HashSet<>(watchlist);
    }

    public HashMap<String, Position> getPositionsByTickers() {
        return new HashMap<>(positionsByTickers);
    }

    void addPosition(Position position) {
        positionsByTickers.put(position.getTicker(), position);
    }

    void setBalances(Money cash, Money reserved) {
        this.cash = cash;
        this.reservedCash = reserved;
    }

    private Money costForTransaction(Transaction transaction, Currency currency) {
        BigDecimal total = transaction.getUnitPrice().multiply(BigDecimal.valueOf(transaction.getQuantity()));
        return Money.of(total, currency);
    }

    public void ensureHasPosition(String ticker, int quantity) {
        Position position = positionsByTickers.get(ticker);
        if (position == null || position.getTotalQuantity() < quantity) {
            throw new InsufficientAssetsException("Not enough assets to sell. Ticker: " + ticker);
        }
    }
}
