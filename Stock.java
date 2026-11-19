/**
 * Stock.java
 * Model class representing a simulated stock in the market.
 * 
 * Follows OOP encapsulation principles with private fields and public accessors.
 */
public class Stock {
    private String symbol;
    private String name;
    private double currentPrice;
    private double previousPrice;

    /**
     * Constructor for a new stock with initial price.
     *
     * @param symbol       Unique stock symbol (e.g., TCS, INFY)
     * @param name         Full company name
     * @param currentPrice Initial simulated stock price
     */
    public Stock(String symbol, String name, double currentPrice) {
        this.symbol = symbol.toUpperCase().trim();
        this.name = name.trim();
        this.currentPrice = currentPrice;
        this.previousPrice = currentPrice;
    }

    /**
     * Constructor restoring stock with current and previous prices.
     *
     * @param symbol        Unique stock symbol
     * @param name          Full company name
     * @param currentPrice  Current simulated stock price
     * @param previousPrice Previous simulated stock price
     */
    public Stock(String symbol, String name, double currentPrice, double previousPrice) {
        this.symbol = symbol.toUpperCase().trim();
        this.name = name.trim();
        this.currentPrice = currentPrice;
        this.previousPrice = previousPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public double getPreviousPrice() {
        return previousPrice;
    }

    /**
     * Updates the stock price while saving the previous price
     * for fluctuation calculations.
     *
     * @param newPrice New simulated price (must be positive)
     */
    public void updatePrice(double newPrice) {
        if (newPrice > 0) {
            this.previousPrice = this.currentPrice;
            this.currentPrice = Math.round(newPrice * 100.0) / 100.0; // Round to 2 decimal places
        }
    }

    /**
     * Calculates the absolute price change from the previous price.
     */
    public double getPriceChange() {
        return Math.round((currentPrice - previousPrice) * 100.0) / 100.0;
    }

    /**
     * Calculates the percentage price change from the previous price.
     */
    public double getPriceChangePercentage() {
        if (previousPrice <= 0) {
            return 0.0;
        }
        double change = ((currentPrice - previousPrice) / previousPrice) * 100.0;
        return Math.round(change * 100.0) / 100.0;
    }

    /**
     * Formats stock data for permanent file storage.
     */
    public String toDataString() {
        return symbol + "|" + name + "|" + currentPrice + "|" + previousPrice;
    }

    /**
     * Parses a data line from storage into a Stock instance.
     */
    public static Stock fromDataString(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        String[] parts = line.split("\\|");
        if (parts.length >= 4) {
            try {
                String symbol = parts[0].trim();
                String name = parts[1].trim();
                double currentPrice = Double.parseDouble(parts[2].trim());
                double previousPrice = Double.parseDouble(parts[3].trim());
                return new Stock(symbol, name, currentPrice, previousPrice);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("%-10s %-28s ₹%,10.2f", symbol, name, currentPrice);
    }
}
