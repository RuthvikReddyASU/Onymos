import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.ThreadLocalRandom;

class Order {
    enum Type { BUY, SELL }

    Type type;
    String ticker;
    int quantity;
    double price;
    volatile Order next; // Lock-free linked list

    public Order(Type type, String ticker, int quantity, double price) {
        this.type = type;
        this.ticker = ticker;
        this.quantity = quantity;
        this.price = price;
        this.next = null;
    }
}

class OrderBook {
    private final AtomicReference<Order> buyOrders = new AtomicReference<>(null);
    private final AtomicReference<Order> sellOrders = new AtomicReference<>(null);

    /**
     * Adds a new order to the order book using lock-free insertion.
     */
    public void addOrder(Order.Type type, String ticker, int quantity, double price) {
        Order newOrder = new Order(type, ticker, quantity, price);
        AtomicReference<Order> head = (type == Order.Type.BUY) ? buyOrders : sellOrders;

        while (true) {
            Order currentHead = head.get();
            newOrder.next = currentHead;
            if (head.compareAndSet(currentHead, newOrder)) {
                break; // Successfully inserted order
            }
        }
    }

    /**
     * Matches and executes buy/sell orders in O(n) time.
     */
    public void matchOrders() {
        Order sellHead = sellOrders.get();
        Order buyHead = buyOrders.get();
        Order prevSell = null, prevBuy = null;

        while (sellHead != null && buyHead != null) {
            if (buyHead.price >= sellHead.price) { // Orders match if buy price ≥ sell price
                int matchedQuantity = Math.min(buyHead.quantity, sellHead.quantity);
                System.out.printf("Executed: %d of %s at $%.2f\n", matchedQuantity, buyHead.ticker, sellHead.price);

                // Adjust remaining quantities
                buyHead.quantity -= matchedQuantity;
                sellHead.quantity -= matchedQuantity;

                // Remove fully executed buy order
                if (buyHead.quantity == 0) {
                    if (prevBuy == null) buyOrders.set(buyHead.next);
                    else prevBuy.next = buyHead.next;
                    buyHead = buyHead.next;
                } else {
                    prevBuy = buyHead;
                    buyHead = buyHead.next;
                }

                // Remove fully executed sell order
                if (sellHead.quantity == 0) {
                    if (prevSell == null) sellOrders.set(sellHead.next);
                    else prevSell.next = sellHead.next;
                    sellHead = sellHead.next;
                } else {
                    prevSell = sellHead;
                    sellHead = sellHead.next;
                }
            } else { // If no match, move to next buy order
                prevBuy = buyHead;
                buyHead = buyHead.next;
            }
        }
    }
}

// Simulates random stock transactions
class StockSimulator {
    private final OrderBook orderBook = new OrderBook();
    private final String[] tickers = new String[1024];

    public StockSimulator() {
        for (int i = 0; i < 1024; i++) tickers[i] = "STOCK" + i;
    }

    /**
     * Simulates a real-time stock market by randomly adding and matching orders.
     */
    public void runSimulation() {
        // Create 1000 random buy/sell orders
        for (int i = 0; i < 1000; i++) {
            Order.Type type = (ThreadLocalRandom.current().nextBoolean()) ? Order.Type.BUY : Order.Type.SELL;
            String ticker = tickers[ThreadLocalRandom.current().nextInt(1024)];
            int quantity = ThreadLocalRandom.current().nextInt(1, 100);
            double price = ThreadLocalRandom.current().nextDouble(10.0, 500.0);
            orderBook.addOrder(type, ticker, quantity, price);
        }

        // Run matching process
        System.out.println("\nMatching orders...");
        orderBook.matchOrders();
    }
}

// Main execution
public class StockExchange {
    public static void main(String[] args) {
        StockSimulator simulator = new StockSimulator();
        simulator.runSimulation();
    }
}
