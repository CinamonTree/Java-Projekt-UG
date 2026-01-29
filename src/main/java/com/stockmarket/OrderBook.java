package com.stockmarket;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

/** Klasa zajmująca się dobieraniem, procedowaniem zleceń kupna i sprzedaży dla konkretnego instrumentu. */
public class OrderBook {
    
    private static int orderSequence = 0;
    private final String ticker;
    private final AssetType assetType;
    private final PriorityQueue<Order> sellOrders;
    private final PriorityQueue<Order> buyOrders;

    private static final Comparator<Order> SELL_ORDER_COMPARATOR = 
        Comparator.comparingInt((Order order) -> order.getOrderType() == OrderType.MARKET ? 0 : 1)
        .thenComparing(Order::getLimitPrice, Comparator.nullsLast(BigDecimal::compareTo))
        .thenComparingInt(Order::getSequence);

    private static final Comparator<Order> BUY_ORDER_COMPARATOR =
        Comparator.comparingInt((Order order) -> order.getOrderType() == OrderType.MARKET ? 0 : 1)
        .thenComparing(Order::getLimitPrice, Comparator.nullsLast(BigDecimal::compareTo).reversed())
        .thenComparingInt(Order::getSequence);

    public OrderBook(String ticker, AssetType assetType) {
        this.ticker = ticker;
        this.assetType = assetType;
        this.sellOrders = new PriorityQueue<>(SELL_ORDER_COMPARATOR);
        this.buyOrders = new PriorityQueue<>(BUY_ORDER_COMPARATOR);
    }

    public void placeSellMarketOrder(String portfolioID, int quantity) {
        int sequence = nextSequence();
        Order sellOrder = new Order(makeOrderId(sequence), portfolioID, OrderSide.SELL, OrderType.MARKET, quantity, null, sequence);
        sellOrders.add(sellOrder);
    }

    public void placeBuyMarketOrder(String portfolioID, int quantity) {
        int sequence = nextSequence();
        Order buyOrder = new Order(makeOrderId(sequence), portfolioID, OrderSide.BUY, OrderType.MARKET, quantity, null, sequence);
        buyOrders.add(buyOrder);
    }

    public void placeSellLimitOrder(String portfolioID, BigDecimal priceLimit, int quantity) {
        int sequence = nextSequence();
        Order sellOrder = new Order(makeOrderId(sequence), portfolioID, OrderSide.SELL, OrderType.LIMIT, quantity, priceLimit, sequence);
        sellOrders.add(sellOrder);
    }

    public void placeBuyLimitOrder(String portfolioID, BigDecimal priceLimit, int quantity) {
        int sequence = nextSequence();
        Order buyOrder = new Order(makeOrderId(sequence), portfolioID, OrderSide.BUY, OrderType.LIMIT, quantity, priceLimit, sequence);
        buyOrders.add(buyOrder);
    }

    /** Proceduje zlecenia zakupu i sprzedaży. Dobiera odpowiednie zlecenia i tworzy z nich listę transakcji które udało się dobrać. */
    public ArrayList<Transaction> process() {
        ArrayList<Transaction> transactions = new ArrayList<>();

        while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
            Order buyOrder = buyOrders.peek();
            Order sellOrder = sellOrders.peek();

            if (!canMatch(buyOrder, sellOrder)) {
                break;
            }

            int matchQuantity = Math.min(buyOrder.getQuantity(), sellOrder.getQuantity());
            BigDecimal tradePrice = resolveTradePrice(buyOrder, sellOrder);

            transactions.add(new Transaction(
                new java.util.Date(),
                ticker,
                assetType,
                buyOrder.getOrderID(),
                sellOrder.getOrderID(),
                buyOrder.getPortfolioID(),
                sellOrder.getPortfolioID(),
                matchQuantity,
                tradePrice
            ));

            buyOrder.reduceQuantity(matchQuantity);
            sellOrder.reduceQuantity(matchQuantity);

            if (buyOrder.getQuantity() == 0) {
                buyOrders.poll();
            }
            if (sellOrder.getQuantity() == 0) {
                sellOrders.poll();
            }
        }

        return transactions;
    }
    
    private boolean canMatch(Order buyOrder, Order sellOrder) {
        if (buyOrder.getOrderType() == OrderType.MARKET || sellOrder.getOrderType() == OrderType.MARKET) {
            return true;
        }
        return buyOrder.getLimitPrice().compareTo(sellOrder.getLimitPrice()) >= 0;
    }

    private BigDecimal resolveTradePrice(Order buyOrder, Order sellOrder) {
        if (buyOrder.getOrderType() == OrderType.MARKET && sellOrder.getOrderType() == OrderType.MARKET) {
            return BigDecimal.ZERO;
        }
        if (buyOrder.getOrderType() == OrderType.MARKET) {
            return sellOrder.getLimitPrice();
        }
        if (sellOrder.getOrderType() == OrderType.MARKET) {
            return buyOrder.getLimitPrice();
        }
        return sellOrder.getLimitPrice();
    }

    private String makeOrderId(int sequence) {
        return ticker + "-" + sequence;
    }

    private int nextSequence() {
        return orderSequence++;
    }

}
