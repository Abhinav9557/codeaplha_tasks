import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * StockMarket.java
 * Manages the simulated stock market, catalog of available stocks,
 * formatted display of market quotes, and realistic price fluctuations.
 * 
 * Uses ArrayList for ordered iteration and HashMap for O(1) symbol lookup.
 */
public class StockMarket {
    private List<Stock> stockList;
    private Map<String, Stock> stockMap;
    private Random random;

    // Minimum price floor to guarantee prices never drop to zero or negative
    public static final double MINIMUM_PRICE_FLOOR = 5.00;

    public StockMarket() {
        this.stockList = new ArrayList<>();
        this.stockMap = new HashMap<>();
        this.random = new Random();
        initializeDefaultStocks();
    }

    /**
     * Seeds the market with the required internship default stocks.
     */
    public void initializeDefaultStocks() {
        stockList.clear();
        stockMap.clear();

        // Required example stocks from specifications
        addStock(new Stock("TCS", "Tata Consultancy Services", 3500.00));
        addStock(new Stock("INFY", "Infosys Limited", 1500.00));
        addStock(new Stock("RELIANCE", "Reliance Industries", 2800.00));
        addStock(new Stock("HDFCBANK", "HDFC Bank", 1700.00));
        addStock(new Stock("WIPRO", "Wipro Limited", 500.00));

        // Additional prominent Indian blue-chip stocks for richer trading
        addStock(new Stock("ICICIBANK", "ICICI Bank Limited", 1100.00));
        addStock(new Stock("ITC", "ITC Limited", 450.00));
        addStock(new Stock("TATAMOTORS", "Tata Motors Limited", 950.00));
        addStock(new Stock("SBIN", "State Bank of India", 800.00));
        addStock(new Stock("BHARTIARTL", "Bharti Airtel Limited", 1300.00));
    }

    /**
     * Adds a stock to both the list and the lookup map.
     */
    public void addStock(Stock stock) {
        if (stock != null) {
            stockList.add(stock);
            stockMap.put(stock.getSymbol().toUpperCase(), stock);
        }
    }

    /**
     * Replaces the current market stocks with restored stocks from file.
     */
    public void setStocks(List<Stock> loadedStocks) {
        if (loadedStocks != null && !loadedStocks.isEmpty()) {
            this.stockList.clear();
            this.stockMap.clear();
            for (Stock s : loadedStocks) {
                addStock(s);
            }
        }
    }

    /**
     * Retrieves a stock by its symbol (case-insensitive).
     *
     * @param symbol Stock ticker (e.g. TCS)
     * @return Stock object, or null if not found
     */
    public Stock getStock(String symbol) {
        if (symbol == null) return null;
        return stockMap.get(symbol.toUpperCase().trim());
    }

    /**
     * Checks if a stock symbol exists in the market.
     */
    public boolean containsStock(String symbol) {
        if (symbol == null) return false;
        return stockMap.containsKey(symbol.toUpperCase().trim());
    }

    public List<Stock> getAllStocks() {
        return new ArrayList<>(stockList);
    }

    /**
     * Refreshes market prices by applying realistic random percentage fluctuations.
     * Fluctuations range from -5.0% to +5.0% per refresh.
     * Guarantees prices never become zero or negative.
     */
    public void refreshPrices() {
        for (Stock stock : stockList) {
            // Generate a random fluctuation between -5.0% and +5.0%
            // (random.nextDouble() yields [0.0, 1.0) -> (nextDouble() * 10.0) - 5.0 gives [-5.0, +5.0)
            double percentChange = (random.nextDouble() * 10.0) - 5.0;
            double currentPrice = stock.getCurrentPrice();
            double newPrice = currentPrice * (1.0 + (percentChange / 100.0));

            // Guarantee price never falls below safety floor
            if (newPrice < MINIMUM_PRICE_FLOOR) {
                newPrice = MINIMUM_PRICE_FLOOR;
            }

            stock.updatePrice(newPrice);
        }
    }

    /**
     * Displays all available stocks in a clean, professional ASCII table.
     */
    public void displayMarket() {
        System.out.println("\n+====================================================================================================+");
        System.out.println("|                                    SIMULATED STOCK MARKET                                          |");
        System.out.println("+------------+--------------------------------+--------------+--------------+--------------+---------+");
        System.out.println("| Symbol     | Stock Name                     | Price (₹)    | Prev. Price  | Change (₹)   | Change% |");
        System.out.println("+------------+--------------------------------+--------------+--------------+--------------+---------+");

        for (Stock stock : stockList) {
            double change = stock.getPriceChange();
            double pctChange = stock.getPriceChangePercentage();
            String directionSign = change >= 0 ? "+" : "";

            System.out.printf("| %-10s | %-30s | ₹%11.2f | ₹%11.2f | %s%11.2f | %s%6.2f%% |\n",
                    stock.getSymbol(),
                    stock.getName(),
                    stock.getCurrentPrice(),
                    stock.getPreviousPrice(),
                    directionSign,
                    change,
                    directionSign,
                    pctChange);
        }
        System.out.println("+------------+--------------------------------+--------------+--------------+--------------+---------+");
        System.out.println("  * Prices are educational simulations and do not represent live real-world stock market data.");
    }
}
