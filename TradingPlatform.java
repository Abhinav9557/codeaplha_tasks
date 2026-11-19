import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * TradingPlatform.java
 * Central controller engine for the Console-Based Stock Trading Platform.
 * 
 * Coordinates the stock market, user portfolio, transaction logging,
 * historical performance snapshots, and permanent file persistence.
 */
public class TradingPlatform {
    public static final double INITIAL_CAPITAL = 100000.00; // ₹100,000 initial virtual balance

    private double cashBalance;
    private StockMarket market;
    private Portfolio portfolio;
    private List<Transaction> transactions;
    private List<PerformanceSnapshot> performanceSnapshots;
    private DataManager dataManager;

    public TradingPlatform() {
        this.market = new StockMarket();
        this.portfolio = new Portfolio();
        this.transactions = new ArrayList<>();
        this.performanceSnapshots = new ArrayList<>();
        this.dataManager = new DataManager();
        this.cashBalance = INITIAL_CAPITAL;
    }

    /**
     * Initializes the platform, restoring saved data from file if present,
     * or initializing a new virtual account with ₹100,000 if first run.
     */
    public void initialize() {
        if (dataManager.hasSavedData()) {
            // Restore previously saved state
            this.cashBalance = dataManager.loadCashBalance(INITIAL_CAPITAL);
            this.dataManager.loadMarket(market);
            this.dataManager.loadPortfolio(portfolio);
            this.transactions = dataManager.loadTransactions();
            this.performanceSnapshots = dataManager.loadPerformanceSnapshots();

            System.out.println("[+] Restored existing session data successfully.");
            System.out.printf("[+] Current Available Cash Balance: ₹%,.2f\n", cashBalance);
            System.out.printf("[+] Active Portfolio Holdings: %d\n", portfolio.getHoldingCount());
            System.out.printf("[+] Total Past Transactions: %d\n", transactions.size());
        } else {
            // First time launch
            this.cashBalance = INITIAL_CAPITAL;
            market.initializeDefaultStocks();
            
            // Record initial baseline snapshot
            PerformanceSnapshot initialSnapshot = new PerformanceSnapshot(
                    "SNP-1001",
                    "INITIAL ACCOUNT CREATION",
                    cashBalance,
                    0.00,
                    cashBalance,
                    0.00,
                    0.00
            );
            performanceSnapshots.add(initialSnapshot);

            // Persist initial state
            saveAllData();

            System.out.println("[+] Welcome! Initialized new virtual account.");
            System.out.printf("[+] Starting Virtual Balance: ₹%,.2f\n", cashBalance);
        }
    }

    public double getCashBalance() {
        return cashBalance;
    }

    public StockMarket getMarket() {
        return market;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    /**
     * Helper to persist all current state to disk.
     */
    public void saveAllData() {
        dataManager.saveAccount(cashBalance, INITIAL_CAPITAL);
        dataManager.savePortfolio(portfolio);
        dataManager.saveTransactions(transactions);
        dataManager.savePerformanceSnapshots(performanceSnapshots);
        dataManager.saveMarket(market);
    }

    /**
     * Records a new performance snapshot and appends it to disk.
     */
    private void recordPerformanceSnapshot(String triggerEvent) {
        String snapshotId = "SNP-" + (performanceSnapshots.size() + 1001);
        double portfolioVal = portfolio.calculateTotalMarketValue(market);
        double totalVal = cashBalance + portfolioVal;
        double pnl = totalVal - INITIAL_CAPITAL;
        double pnlPct = (INITIAL_CAPITAL > 0) ? (pnl / INITIAL_CAPITAL) * 100.0 : 0.0;

        PerformanceSnapshot snapshot = new PerformanceSnapshot(
                snapshotId,
                triggerEvent,
                cashBalance,
                portfolioVal,
                totalVal,
                pnl,
                pnlPct
        );
        performanceSnapshots.add(snapshot);
        dataManager.appendPerformanceSnapshot(snapshot);
    }

    // ==========================================
    // 1. VIEW MARKET
    // ==========================================
    public void viewMarket() {
        market.displayMarket();
    }

    // ==========================================
    // 2. BUY STOCKS
    // ==========================================
    public void buyStock(Scanner scanner) {
        System.out.println("\n--------------------------------------------------");
        System.out.println("                 BUY STOCKS                       ");
        System.out.println("--------------------------------------------------");
        market.displayMarket();

        System.out.printf("\nYour Available Cash: ₹%,.2f\n", cashBalance);
        System.out.print("Enter Stock Symbol to Buy (e.g. TCS) or 0 to Cancel: ");
        String symbolInput = scanner.nextLine().trim();

        if (symbolInput.equals("0") || symbolInput.isEmpty()) {
            System.out.println("[*] Purchase cancelled.");
            return;
        }

        String symbol = symbolInput.toUpperCase();
        Stock stock = market.getStock(symbol);

        if (stock == null) {
            System.out.printf("[!] Error: Stock symbol '%s' was not found in the market catalog.\n", symbol);
            return;
        }

        double stockPrice = stock.getCurrentPrice();
        int maxAffordable = (int) (cashBalance / stockPrice);

        System.out.printf("\nSelected Stock : %s (%s)\n", stock.getName(), stock.getSymbol());
        System.out.printf("Current Price  : ₹%,.2f per share\n", stockPrice);
        System.out.printf("Max Affordable : %d shares\n", maxAffordable);

        if (maxAffordable <= 0) {
            System.out.println("[!] Insufficient balance to buy even 1 share of this stock.");
            return;
        }

        int quantity = 0;
        while (true) {
            System.out.print("Enter quantity to purchase (Whole positive number): ");
            String qtyStr = scanner.nextLine().trim();
            try {
                quantity = Integer.parseInt(qtyStr);
                if (quantity <= 0) {
                    System.out.println("[!] Quantity must be greater than zero. Please enter a valid number.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid input. Please enter a whole integer number.");
            }
        }

        double totalCost = Math.round((quantity * stockPrice) * 100.0) / 100.0;

        if (totalCost > cashBalance) {
            System.out.printf("[!] Insufficient Funds! Total cost is ₹%,.2f but you only have ₹%,.2f.\n",
                    totalCost, cashBalance);
            System.out.printf("[!] Maximum shares you can purchase right now: %d\n", maxAffordable);
            return;
        }

        System.out.println("\n----------------- ORDER CONFIRMATION -----------------");
        System.out.printf(" Stock           : %s (%s)\n", stock.getName(), stock.getSymbol());
        System.out.printf(" Quantity        : %d shares\n", quantity);
        System.out.printf(" Price per Share : ₹%,.2f\n", stockPrice);
        System.out.printf(" Total Cost      : ₹%,.2f\n", totalCost);
        System.out.printf(" Remaining Cash  : ₹%,.2f\n", (cashBalance - totalCost));
        System.out.println("------------------------------------------------------");
        System.out.print("Confirm this purchase? (Y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (!confirm.isEmpty() && !confirm.equals("y") && !confirm.equals("yes")) {
            System.out.println("[*] Purchase order cancelled by user.");
            return;
        }

        // Deduct balance and update portfolio
        cashBalance = Math.round((cashBalance - totalCost) * 100.0) / 100.0;
        portfolio.addShares(stock, quantity, stockPrice);

        // Record transaction
        String txnId = "TXN-" + (transactions.size() + 1001);
        Transaction txn = new Transaction(
                txnId,
                "BUY",
                stock.getSymbol(),
                stock.getName(),
                quantity,
                stockPrice,
                totalCost,
                cashBalance
        );
        transactions.add(txn);
        dataManager.appendTransaction(txn);

        // Record performance snapshot
        recordPerformanceSnapshot(String.format("BUY %s (%d shares @ ₹%,.2f)", stock.getSymbol(), quantity, stockPrice));

        // Save updated data
        dataManager.saveAccount(cashBalance, INITIAL_CAPITAL);
        dataManager.savePortfolio(portfolio);

        Holding updatedHolding = portfolio.getHolding(symbol);
        System.out.println("\n==================================================");
        System.out.println("       ✓ PURCHASE EXECUTED SUCCESSFULLY!          ");
        System.out.println("==================================================");
        System.out.printf(" Transaction ID   : %s\n", txnId);
        System.out.printf(" Stock Purchased  : %s (%s)\n", stock.getName(), stock.getSymbol());
        System.out.printf(" Shares Bought    : %d\n", quantity);
        System.out.printf(" Total Shares Now : %d\n", updatedHolding != null ? updatedHolding.getQuantity() : quantity);
        System.out.printf(" New Avg Buy Price: ₹%,.2f\n", updatedHolding != null ? updatedHolding.getAverageBuyPrice() : stockPrice);
        System.out.printf(" Total Paid       : ₹%,.2f\n", totalCost);
        System.out.printf(" New Cash Balance : ₹%,.2f\n", cashBalance);
        System.out.println("==================================================");
    }

    // ==========================================
    // 3. SELL STOCKS
    // ==========================================
    public void sellStock(Scanner scanner) {
        System.out.println("\n--------------------------------------------------");
        System.out.println("                 SELL STOCKS                      ");
        System.out.println("--------------------------------------------------");

        if (portfolio.isEmpty()) {
            System.out.println("[!] You do not have any stocks in your portfolio to sell.");
            System.out.println("[*] Use option 2 from the main menu to buy stocks first.");
            return;
        }

        // Display current holdings
        portfolio.displayPortfolio(market, cashBalance, INITIAL_CAPITAL);

        System.out.print("\nEnter Stock Symbol to Sell or 0 to Cancel: ");
        String symbolInput = scanner.nextLine().trim();

        if (symbolInput.equals("0") || symbolInput.isEmpty()) {
            System.out.println("[*] Sell order cancelled.");
            return;
        }

        String symbol = symbolInput.toUpperCase();
        Holding holding = portfolio.getHolding(symbol);

        if (holding == null || holding.getQuantity() <= 0) {
            System.out.printf("[!] Error: You do not own any shares of '%s'.\n", symbol);
            return;
        }

        Stock stock = market.getStock(symbol);
        double currentPrice = (stock != null) ? stock.getCurrentPrice() : holding.getAverageBuyPrice();
        String stockName = holding.getName();
        int ownedQty = holding.getQuantity();
        double avgBuyPrice = holding.getAverageBuyPrice();

        System.out.printf("\nSelected Holding: %s (%s)\n", stockName, symbol);
        System.out.printf("Shares Owned    : %d\n", ownedQty);
        System.out.printf("Avg Buy Price   : ₹%,.2f\n", avgBuyPrice);
        System.out.printf("Current Price   : ₹%,.2f\n", currentPrice);

        int quantityToSell = 0;
        while (true) {
            System.out.printf("Enter quantity to sell (1 to %d): ", ownedQty);
            String qtyStr = scanner.nextLine().trim();
            try {
                quantityToSell = Integer.parseInt(qtyStr);
                if (quantityToSell <= 0) {
                    System.out.println("[!] Quantity must be greater than zero.");
                    continue;
                }
                if (quantityToSell > ownedQty) {
                    System.out.printf("[!] Cannot sell %d shares! You only own %d shares of %s.\n",
                            quantityToSell, ownedQty, symbol);
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid input. Please enter a whole integer number.");
            }
        }

        double totalProceeds = Math.round((quantityToSell * currentPrice) * 100.0) / 100.0;
        double costBasisOfSoldShares = Math.round((quantityToSell * avgBuyPrice) * 100.0) / 100.0;
        double realizedPnl = Math.round((totalProceeds - costBasisOfSoldShares) * 100.0) / 100.0;
        double realizedPnlPct = (costBasisOfSoldShares > 0) ? (realizedPnl / costBasisOfSoldShares) * 100.0 : 0.0;

        String pnlFormatted = (realizedPnl >= 0)
                ? String.format("+₹%,.2f (+%.2f%%)", realizedPnl, realizedPnlPct)
                : String.format("-₹%,.2f (%.2f%%)", Math.abs(realizedPnl), realizedPnlPct);

        System.out.println("\n----------------- SELL ORDER SUMMARY -----------------");
        System.out.printf(" Stock           : %s (%s)\n", stockName, symbol);
        System.out.printf(" Quantity to Sell: %d shares\n", quantityToSell);
        System.out.printf(" Selling Price   : ₹%,.2f per share\n", currentPrice);
        System.out.printf(" Total Proceeds  : ₹%,.2f\n", totalProceeds);
        System.out.printf(" Realized P/L    : %s\n", pnlFormatted);
        System.out.printf(" New Cash Balance: ₹%,.2f\n", (cashBalance + totalProceeds));
        System.out.println("------------------------------------------------------");
        System.out.print("Confirm this sale? (Y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (!confirm.isEmpty() && !confirm.equals("y") && !confirm.equals("yes")) {
            System.out.println("[*] Sell order cancelled by user.");
            return;
        }

        // Credit balance and update portfolio
        cashBalance = Math.round((cashBalance + totalProceeds) * 100.0) / 100.0;
        portfolio.sellShares(symbol, quantityToSell);

        // Record transaction
        String txnId = "TXN-" + (transactions.size() + 1001);
        Transaction txn = new Transaction(
                txnId,
                "SELL",
                symbol,
                stockName,
                quantityToSell,
                currentPrice,
                totalProceeds,
                cashBalance
        );
        transactions.add(txn);
        dataManager.appendTransaction(txn);

        // Record performance snapshot
        recordPerformanceSnapshot(String.format("SELL %s (%d shares @ ₹%,.2f)", symbol, quantityToSell, currentPrice));

        // Save updated data
        dataManager.saveAccount(cashBalance, INITIAL_CAPITAL);
        dataManager.savePortfolio(portfolio);

        Holding remainingHolding = portfolio.getHolding(symbol);
        int remainingShares = (remainingHolding != null) ? remainingHolding.getQuantity() : 0;

        System.out.println("\n==================================================");
        System.out.println("         ✓ SALE EXECUTED SUCCESSFULLY!            ");
        System.out.println("==================================================");
        System.out.printf(" Transaction ID    : %s\n", txnId);
        System.out.printf(" Stock Sold        : %s (%s)\n", stockName, symbol);
        System.out.printf(" Shares Sold       : %d\n", quantityToSell);
        System.out.printf(" Shares Remaining  : %d\n", remainingShares);
        System.out.printf(" Total Cash Added  : ₹%,.2f\n", totalProceeds);
        System.out.printf(" Realized P/L      : %s\n", pnlFormatted);
        System.out.printf(" New Cash Balance  : ₹%,.2f\n", cashBalance);
        System.out.println("==================================================");
    }

    // ==========================================
    // 4. VIEW PORTFOLIO & PROFIT/LOSS SUMMARY
    // ==========================================
    public void viewPortfolio() {
        portfolio.displayPortfolio(market, cashBalance, INITIAL_CAPITAL);
    }

    // ==========================================
    // 5. REFRESH MARKET PRICES (SIMULATION)
    // ==========================================
    public void refreshMarketPrices() {
        System.out.println("\n====================================================================================================");
        System.out.println("                         SIMULATING MARKET TICK (PRICE FLUCTUATIONS)                                ");
        System.out.println("====================================================================================================");
        System.out.println("[*] Applying realistic market price volatility (-5.0% to +5.0%)...");

        market.refreshPrices();
        market.displayMarket();

        // Calculate and show real-time updated portfolio impact
        double portfolioVal = portfolio.calculateTotalMarketValue(market);
        double totalVal = cashBalance + portfolioVal;
        double overallPnl = totalVal - INITIAL_CAPITAL;
        double overallPnlPct = (overallPnl / INITIAL_CAPITAL) * 100.0;

        String pnlFormatted = (overallPnl >= 0)
                ? String.format("+₹%,.2f (+%.2f%%)", overallPnl, overallPnlPct)
                : String.format("-₹%,.2f (%.2f%%)", Math.abs(overallPnl), overallPnlPct);

        System.out.println("\n------------------------ UPDATED PORTFOLIO VALUATION ------------------------");
        System.out.printf(" Total Portfolio Value : ₹%,.2f\n", portfolioVal);
        System.out.printf(" Available Cash        : ₹%,.2f\n", cashBalance);
        System.out.printf(" Total Account Value   : ₹%,.2f\n", totalVal);
        System.out.printf(" Overall Profit / Loss : %s\n", pnlFormatted);
        System.out.println("-----------------------------------------------------------------------------");

        // Record performance snapshot
        recordPerformanceSnapshot("MARKET REFRESH (Simulated Tick)");

        // Save updated market quotes and state
        dataManager.saveMarket(market);

        System.out.println("[+] Market prices refreshed and performance snapshot recorded successfully!");
    }

    // ==========================================
    // 6. VIEW TRANSACTION HISTORY
    // ==========================================
    public void viewTransactionHistory() {
        System.out.println("\n+========================================================================================================================+");
        System.out.println("|                                               TRANSACTION AUDIT LOG                                                    |");
        System.out.println("+----------+---------------------+------+------------+--------------------------+-----+-------------+--------------+-------------+");
        System.out.println("| Txn ID   | Date & Time         | Type | Symbol     | Stock Name               | Qty | Price (₹)   | Total (₹)    | Balance (₹) |");
        System.out.println("+----------+---------------------+------+------------+--------------------------+-----+-------------+--------------+-------------+");

        if (transactions.isEmpty()) {
            System.out.println("|                                             No transactions executed yet!                                              |");
            System.out.println("+----------+---------------------+------+------------+--------------------------+-----+-------------+--------------+-------------+");
            return;
        }

        for (Transaction t : transactions) {
            System.out.printf("| %-8s | %-19s | %-4s | %-10s | %-24s | %3d | ₹%10.2f | ₹%11.2f | ₹%10.2f |\n",
                    t.getTransactionId(),
                    t.getTimestamp(),
                    t.getType(),
                    t.getSymbol(),
                    (t.getStockName().length() > 24 ? t.getStockName().substring(0, 21) + "..." : t.getStockName()),
                    t.getQuantity(),
                    t.getPricePerShare(),
                    t.getTotalAmount(),
                    t.getBalanceAfter());
        }
        System.out.println("+----------+---------------------+------+------------+--------------------------+-----+-------------+--------------+-------------+");
        System.out.printf(" Total Transactions Executed: %d\n", transactions.size());
    }

    // ==========================================
    // 7. VIEW PERFORMANCE TRACKING HISTORY
    // ==========================================
    public void viewPerformanceHistory() {
        System.out.println("\n+=====================================================================================================================================+");
        System.out.println("|                                            HISTORICAL PORTFOLIO PERFORMANCE SNAPSHOTS                                               |");
        System.out.println("+----------+---------------------+-----------------------------------+--------------+--------------+--------------+-------------------+");
        System.out.println("| Snap ID  | Timestamp           | Trigger Event                     | Cash Balance | Portfolio Val| Total Value  | Profit / Loss     |");
        System.out.println("+----------+---------------------+-----------------------------------+--------------+--------------+--------------+-------------------+");

        if (performanceSnapshots.isEmpty()) {
            System.out.println("|                                           No performance snapshots recorded yet!                                                    |");
            System.out.println("+----------+---------------------+-----------------------------------+--------------+--------------+--------------+-------------------+");
            return;
        }

        for (PerformanceSnapshot s : performanceSnapshots) {
            double pnl = s.getProfitLoss();
            double pnlPct = s.getProfitLossPercentage();
            String pnlFormatted = (pnl >= 0)
                    ? String.format("+₹%,.2f (+%.2f%%)", pnl, pnlPct)
                    : String.format("-₹%,.2f (%.2f%%)", Math.abs(pnl), pnlPct);

            String event = s.getTriggerEvent();
            if (event.length() > 33) {
                event = event.substring(0, 30) + "...";
            }

            System.out.printf("| %-8s | %-19s | %-33s | ₹%11.2f | ₹%11.2f | ₹%11.2f | %-17s |\n",
                    s.getSnapshotId(),
                    s.getTimestamp(),
                    event,
                    s.getCashBalance(),
                    s.getPortfolioValue(),
                    s.getTotalAccountValue(),
                    pnlFormatted);
        }
        System.out.println("+----------+---------------------+-----------------------------------+--------------+--------------+--------------+-------------------+");
        System.out.printf(" Total Performance Snapshots: %d\n", performanceSnapshots.size());
    }

    // ==========================================
    // 8. RESET ACCOUNT DATA
    // ==========================================
    public void resetAccount(Scanner scanner) {
        System.out.println("\n[!] CAUTION: You are about to reset all trading platform data!");
        System.out.println("    This will permanently clear your portfolio, transaction history,");
        System.out.println("    performance snapshots, and restore the cash balance to ₹100,000.00.");
        System.out.print("Are you sure you want to proceed? Type 'RESET' to confirm: ");
        String confirmation = scanner.nextLine().trim();

        if (confirmation.equals("RESET")) {
            dataManager.resetAllData();
            cashBalance = INITIAL_CAPITAL;
            portfolio.clear();
            transactions.clear();
            performanceSnapshots.clear();
            market.initializeDefaultStocks();

            // Record new initial snapshot
            PerformanceSnapshot initial = new PerformanceSnapshot(
                    "SNP-1001",
                    "RESET TO INITIAL CAPITAL",
                    cashBalance,
                    0.00,
                    cashBalance,
                    0.00,
                    0.00
            );
            performanceSnapshots.add(initial);

            saveAllData();

            System.out.println("[+] All platform data has been reset to defaults!");
            System.out.printf("[+] Account balance restored to initial ₹%,.2f.\n", cashBalance);
        } else {
            System.out.println("[*] Reset cancelled. No data was modified.");
        }
    }
}
