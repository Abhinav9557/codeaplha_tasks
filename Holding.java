/**
 * Holding.java
 * Model class representing a specific stock holding inside the user's portfolio.
 * 
 * Tracks owned quantity and calculates the weighted average purchase price
 * when additional shares are acquired.
 */
public class Holding {
    private String symbol;
    private String name;
    private int quantity;
    private double averageBuyPrice;

    /**
     * Constructs a new stock holding.
     *
     * @param symbol          Stock ticker symbol
     * @param name            Company name
     * @param quantity        Number of shares owned
     * @param averageBuyPrice Average price paid per share
     */
    public Holding(String symbol, String name, int quantity, double averageBuyPrice) {
        this.symbol = symbol.toUpperCase().trim();
        this.name = name.trim();
        this.quantity = quantity;
        this.averageBuyPrice = Math.round(averageBuyPrice * 100.0) / 100.0;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getAverageBuyPrice() {
        return averageBuyPrice;
    }

    /**
     * Adds shares to this holding and recalculates the weighted average purchase price.
     *
     * Formula:
     * New Avg Price = ( (Old Qty * Old Avg Price) + (New Qty * Buy Price) ) / (Old Qty + New Qty)
     *
     * @param additionalQuantity Quantity of additional shares purchased
     * @param purchasePrice      Price per share for this purchase
     */
    public void addShares(int additionalQuantity, double purchasePrice) {
        if (additionalQuantity <= 0 || purchasePrice <= 0) {
            return;
        }
        double currentTotalInvested = this.quantity * this.averageBuyPrice;
        double additionalInvested = additionalQuantity * purchasePrice;
        this.quantity += additionalQuantity;
        this.averageBuyPrice = Math.round(((currentTotalInvested + additionalInvested) / this.quantity) * 100.0) / 100.0;
    }

    /**
     * Reduces the number of shares owned upon selling.
     * (Average purchase price per share remains unchanged).
     *
     * @param quantityToSell Number of shares sold
     * @return true if shares were deducted successfully, false if quantity was invalid
     */
    public boolean removeShares(int quantityToSell) {
        if (quantityToSell <= 0 || quantityToSell > this.quantity) {
            return false;
        }
        this.quantity -= quantityToSell;
        return true;
    }

    /**
     * Total purchase value (cost basis) of this holding.
     */
    public double getTotalInvested() {
        return Math.round((quantity * averageBuyPrice) * 100.0) / 100.0;
    }

    /**
     * Current market value of this holding based on the current market price.
     */
    public double getCurrentValue(double currentPrice) {
        return Math.round((quantity * currentPrice) * 100.0) / 100.0;
    }

    /**
     * Absolute profit or loss on this holding.
     */
    public double getProfitLoss(double currentPrice) {
        return Math.round((getCurrentValue(currentPrice) - getTotalInvested()) * 100.0) / 100.0;
    }

    /**
     * Percentage profit or loss on this holding.
     */
    public double getProfitLossPercentage(double currentPrice) {
        if (averageBuyPrice <= 0) {
            return 0.0;
        }
        double pnlPercent = ((currentPrice - averageBuyPrice) / averageBuyPrice) * 100.0;
        return Math.round(pnlPercent * 100.0) / 100.0;
    }

    /**
     * Formats holding data for file persistence.
     */
    public String toDataString() {
        return symbol + "|" + name + "|" + quantity + "|" + averageBuyPrice;
    }

    /**
     * Parses a data line into a Holding instance.
     */
    public static Holding fromDataString(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        String[] parts = line.split("\\|");
        if (parts.length >= 4) {
            try {
                String symbol = parts[0].trim();
                String name = parts[1].trim();
                int qty = Integer.parseInt(parts[2].trim());
                double avgPrice = Double.parseDouble(parts[3].trim());
                return new Holding(symbol, name, qty, avgPrice);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
