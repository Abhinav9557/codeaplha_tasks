import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Portfolio.java
 * Manages the user's stock holdings, calculating position-level and portfolio-level
 * valuations, profit/loss, and weighted average cost bases.
 * 
 * Uses a HashMap<String, Holding> for O(1) symbol lookups and ArrayList<Holding>
 * for ordered tabular iteration.
 */
public class Portfolio {
    private Map<String, Holding> holdingsMap;

    public Portfolio() {
        this.holdingsMap = new HashMap<>();
    }

    /**
     * Checks if the portfolio contains any shares of the given stock.
     */
    public boolean hasHolding(String symbol) {
        if (symbol == null) return false;
        Holding holding = holdingsMap.get(symbol.toUpperCase().trim());
        return holding != null && holding.getQuantity() > 0;
    }

    /**
     * Retrieves a holding by symbol.
     */
    public Holding getHolding(String symbol) {
        if (symbol == null) return null;
        return holdingsMap.get(symbol.toUpperCase().trim());
    }

    /**
     * Adds shares of a stock to the portfolio.
     * If already owned, updates the weighted average purchase price.
     *
     * @param stock         The stock being purchased
     * @param quantity      Number of shares bought
     * @param purchasePrice Price paid per share
     */
    public void addShares(Stock stock, int quantity, double purchasePrice) {
        if (stock == null || quantity <= 0 || purchasePrice <= 0) {
            return;
        }
        String symbol = stock.getSymbol().toUpperCase().trim();
        if (holdingsMap.containsKey(symbol)) {
            Holding existing = holdingsMap.get(symbol);
            existing.addShares(quantity, purchasePrice);
        } else {
            Holding newHolding = new Holding(symbol, stock.getName(), quantity, purchasePrice);
            holdingsMap.put(symbol, newHolding);
        }
    }

    /**
     * Directly inserts a holding (used during file restoration).
     */
    public void putHolding(Holding holding) {
        if (holding != null && holding.getQuantity() > 0) {
            holdingsMap.put(holding.getSymbol().toUpperCase().trim(), holding);
        }
    }

    /**
     * Sells shares of a stock from the portfolio.
     *
     * @param symbol         Stock symbol
     * @param quantityToSell Number of shares to sell
     * @return true if successfully deducted, false otherwise
     */
    public boolean sellShares(String symbol, int quantityToSell) {
        if (symbol == null || quantityToSell <= 0) {
            return false;
        }
        String key = symbol.toUpperCase().trim();
        Holding holding = holdingsMap.get(key);
        if (holding == null || holding.getQuantity() < quantityToSell) {
            return false;
        }

        boolean success = holding.removeShares(quantityToSell);
        if (success && holding.getQuantity() == 0) {
            // Remove from map if zero shares remain
            holdingsMap.remove(key);
        }
        return success;
    }

    public boolean isEmpty() {
        return holdingsMap.isEmpty();
    }

    public int getHoldingCount() {
        return holdingsMap.size();
    }

    /**
     * Returns a copy of all active holdings as an ArrayList.
     */
    public List<Holding> getAllHoldings() {
        return new ArrayList<>(holdingsMap.values());
    }

    /**
     * Clears all holdings from portfolio.
     */
    public void clear() {
        holdingsMap.clear();
    }

    /**
     * Calculates the total cost basis (amount invested) across all current holdings.
     */
    public double calculateTotalInvested() {
        double total = 0.0;
        for (Holding h : holdingsMap.values()) {
            total += h.getTotalInvested();
        }
        return Math.round(total * 100.0) / 100.0;
    }

    /**
     * Calculates the total current market value of all holdings based on current market quotes.
     */
    public double calculateTotalMarketValue(StockMarket market) {
        double total = 0.0;
        for (Holding h : holdingsMap.values()) {
            Stock stock = market.getStock(h.getSymbol());
            double currentPrice = stock != null ? stock.getCurrentPrice() : h.getAverageBuyPrice();
            total += h.getCurrentValue(currentPrice);
        }
        return Math.round(total * 100.0) / 100.0;
    }

    /**
     * Calculates the unrealized profit or loss across all current holdings.
     */
    public double calculateTotalUnrealizedProfitLoss(StockMarket market) {
        return Math.round((calculateTotalMarketValue(market) - calculateTotalInvested()) * 100.0) / 100.0;
    }

    /**
     * Displays the complete portfolio and profit & loss summary.
     *
     * @param market         Current stock market quotes
     * @param cashBalance    Available liquid cash
     * @param initialCapital Initial starting capital (e.g. ₹100,000)
     */
    public void displayPortfolio(StockMarket market, double cashBalance, double initialCapital) {
        System.out.println("\n+===================================================================================================================================+");
        System.out.println("|                                                        USER PORTFOLIO                                                             |");
        System.out.println("+------------+--------------------------------+-----+-------------+--------------+-------------+--------------+---------------------+");
        System.out.println("| Symbol     | Stock Name                     | Qty | Avg Price   | Total Cost   | Mkt Price   | Market Value | Profit / Loss (₹, %) |");
        System.out.println("+------------+--------------------------------+-----+-------------+--------------+-------------+--------------+---------------------+");

        if (holdingsMap.isEmpty()) {
            System.out.println("|                                           No stock holdings in your portfolio yet!                                                |");
            System.out.println("+------------+--------------------------------+-----+-------------+--------------+-------------+--------------+---------------------+");
        } else {
            for (Holding holding : holdingsMap.values()) {
                Stock stock = market.getStock(holding.getSymbol());
                double currentPrice = (stock != null) ? stock.getCurrentPrice() : holding.getAverageBuyPrice();
                double totalCost = holding.getTotalInvested();
                double currentValue = holding.getCurrentValue(currentPrice);
                double pnl = holding.getProfitLoss(currentPrice);
                double pnlPct = holding.getProfitLossPercentage(currentPrice);

                String pnlFormatted = (pnl >= 0)
                        ? String.format("+₹%,.2f (+%.2f%%)", pnl, pnlPct)
                        : String.format("-₹%,.2f (%.2f%%)", Math.abs(pnl), pnlPct);

                System.out.printf("| %-10s | %-30s | %3d | ₹%10.2f | ₹%11.2f | ₹%10.2f | ₹%11.2f | %-19s |\n",
                        holding.getSymbol(),
                        holding.getName(),
                        holding.getQuantity(),
                        holding.getAverageBuyPrice(),
                        totalCost,
                        currentPrice,
                        currentValue,
                        pnlFormatted);
            }
            System.out.println("+------------+--------------------------------+-----+-------------+--------------+-------------+--------------+---------------------+");
        }

        // Section 6: Comprehensive Profit and Loss Calculation
        double totalInvested = calculateTotalInvested();
        double portfolioMarketValue = calculateTotalMarketValue(market);
        double totalAccountValue = cashBalance + portfolioMarketValue;
        double overallProfitLoss = totalAccountValue - initialCapital;
        double overallProfitLossPercentage = (initialCapital > 0) ? (overallProfitLoss / initialCapital) * 100.0 : 0.0;

        String overallPnlFormatted = (overallProfitLoss >= 0)
                ? String.format("+₹%,.2f", overallProfitLoss)
                : String.format("-₹%,.2f", Math.abs(overallProfitLoss));

        String overallPnlPctFormatted = (overallProfitLossPercentage >= 0)
                ? String.format("+%.2f%%", overallProfitLossPercentage)
                : String.format("%.2f%%", overallProfitLossPercentage);

        String pnlStatus = overallProfitLoss >= 0 ? "PROFIT" : "LOSS";

        System.out.println("\n+--------------------------------------------------------------------------------+");
        System.out.println("|                     FINANCIAL & PROFIT / LOSS SUMMARY                          |");
        System.out.println("+--------------------------------------------------------------------------------+");
        System.out.printf("| Total Invested in Stocks (Cost Basis) : ₹%,16.2f                     |\n", totalInvested);
        System.out.printf("| Current Portfolio Market Value        : ₹%,16.2f                     |\n", portfolioMarketValue);
        System.out.printf("| Available Cash Balance                : ₹%,16.2f                     |\n", cashBalance);
        System.out.println("+--------------------------------------------------------------------------------+");
        System.out.printf("| Total Account Value (Cash + Stocks)   : ₹%,16.2f                     |\n", totalAccountValue);
        System.out.printf("| Initial Investment Capital            : ₹%,16.2f                     |\n", initialCapital);
        System.out.println("+--------------------------------------------------------------------------------+");
        System.out.printf("| Overall Profit / Loss (%-6s)        : %17s                      |\n", pnlStatus, overallPnlFormatted);
        System.out.printf("| Overall Profit / Loss Percentage      : %17s                      |\n", overallPnlPctFormatted);
        System.out.println("+--------------------------------------------------------------------------------+");
    }
}
