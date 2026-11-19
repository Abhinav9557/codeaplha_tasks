import java.util.Scanner;

/**
 * Main.java
 * Console-Based Stock Trading Platform
 * Educational Stock Trading Simulation (Core Java)
 *
 * Project Features:
 * 1. Initial Virtual Balance of ₹100,000 (restored on restart if saved data exists).
 * 2. Simulated Stock Market with formatted tabular quotes.
 * 3. Complete Buy Stocks workflow with fund & quantity validations.
 * 4. Complete Sell Stocks workflow with portfolio ownership & quantity validations.
 * 5. Full Portfolio Management with weighted average purchase prices.
 * 6. Detailed Profit and Loss Analytics (Invested, Portfolio Value, Account Value, Overall P/L).
 * 7. Simulated Market with realistic price fluctuations (-5% to +5%, prices never <= 0).
 * 8. Historical Performance Tracking Snapshots recorded on refresh, buy, and sell.
 * 9. Permanent File Handling (BufferedReader, BufferedWriter) across all entities.
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TradingPlatform platform = new TradingPlatform();

        printHeaderBanner();
        platform.initialize();

        boolean running = true;
        while (running) {
            printStatusBar(platform);
            printMenu();

            System.out.print("Enter your choice (1-9): ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    platform.viewMarket();
                    break;
                case "2":
                    platform.buyStock(scanner);
                    break;
                case "3":
                    platform.sellStock(scanner);
                    break;
                case "4":
                    platform.viewPortfolio();
                    break;
                case "5":
                    platform.refreshMarketPrices();
                    break;
                case "6":
                    platform.viewTransactionHistory();
                    break;
                case "7":
                    platform.viewPerformanceHistory();
                    break;
                case "8":
                    platform.resetAccount(scanner);
                    break;
                case "9":
                    running = false;
                    platform.saveAllData();
                    printExitMessage(platform);
                    break;
                default:
                    System.out.println("\n[!] Invalid choice! Please enter a number between 1 and 9.");
                    break;
            }

            if (running) {
                System.out.print("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }

        scanner.close();
    }

    private static void printHeaderBanner() {
        System.out.println("==========================================================================================");
        System.out.println("*                     CONSOLE-BASED STOCK TRADING PLATFORM                               *");
        System.out.println("*                   Core Java Simulation & Portfolio Tracker                             *");
        System.out.println("==========================================================================================");
    }

    private static void printStatusBar(TradingPlatform platform) {
        double cash = platform.getCashBalance();
        double portfolioVal = platform.getPortfolio().calculateTotalMarketValue(platform.getMarket());
        double totalVal = cash + portfolioVal;
        double pnl = totalVal - TradingPlatform.INITIAL_CAPITAL;
        double pnlPct = (TradingPlatform.INITIAL_CAPITAL > 0) ? (pnl / TradingPlatform.INITIAL_CAPITAL) * 100.0 : 0.0;

        String pnlFormatted = (pnl >= 0)
                ? String.format("+₹%,.2f (+%.2f%%)", pnl, pnlPct)
                : String.format("-₹%,.2f (%.2f%%)", Math.abs(pnl), pnlPct);

        System.out.println("\n+-----------------------------------------------------------------------------------------+");
        System.out.printf("| CASH: ₹%-12.2f | PORTFOLIO: ₹%-12.2f | TOTAL: ₹%-12.2f | P/L: %-19s |\n",
                cash, portfolioVal, totalVal, pnlFormatted);
        System.out.println("+-----------------------------------------------------------------------------------------+");
    }

    private static void printMenu() {
        System.out.println("================================== MAIN MENU ==================================");
        System.out.println("  1. View Stock Market Quotes");
        System.out.println("  2. Buy Stocks");
        System.out.println("  3. Sell Stocks");
        System.out.println("  4. View Portfolio & Profit / Loss Summary");
        System.out.println("  5. Refresh Market Prices (Simulate Market Fluctuations)");
        System.out.println("  6. View Transaction Audit History");
        System.out.println("  7. View Portfolio Performance Tracking Snapshots");
        System.out.println("  8. Reset Account Data (Start Fresh Virtual Simulation)");
        System.out.println("  9. Save & Exit Platform");
        System.out.println("===============================================================================");
    }

    private static void printExitMessage(TradingPlatform platform) {
        System.out.println("\n==========================================================================================");
        System.out.println("  All session data, portfolio holdings, and transaction logs have been saved to disk.    ");
        System.out.printf("  Final Account Value: ₹%,.2f\n",
                platform.getCashBalance() + platform.getPortfolio().calculateTotalMarketValue(platform.getMarket()));
        System.out.println("  Thank you for using the Console-Based Stock Trading Platform!");
        System.out.println("==========================================================================================\n");
    }
}
