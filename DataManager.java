import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * DataManager.java
 * Handles permanent file storage and data retrieval for the trading platform.
 * 
 * Implements robust Core Java File I/O with UTF-8 character encoding to save
 * and restore account balance, portfolio holdings, transaction records,
 * performance snapshots, and market quotes across program restarts.
 */
public class DataManager {
    private static final String DATA_DIR = "data";
    private static final String ACCOUNT_FILE = DATA_DIR + File.separator + "account.txt";
    private static final String PORTFOLIO_FILE = DATA_DIR + File.separator + "portfolio.txt";
    private static final String TRANSACTIONS_FILE = DATA_DIR + File.separator + "transactions.txt";
    private static final String PERFORMANCE_FILE = DATA_DIR + File.separator + "performance_history.txt";
    private static final String MARKET_FILE = DATA_DIR + File.separator + "market.txt";

    public DataManager() {
        ensureDataDirectoryExists();
    }

    /**
     * Ensures the data folder exists on disk.
     */
    private void ensureDataDirectoryExists() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Checks if saved account data already exists on disk.
     */
    public boolean hasSavedData() {
        File accFile = new File(ACCOUNT_FILE);
        return accFile.exists() && accFile.length() > 0;
    }

    // ==========================================
    // ACCOUNT BALANCE PERSISTENCE
    // ==========================================

    /**
     * Saves account balance to file using UTF-8 encoding.
     */
    public void saveAccount(double cashBalance, double initialCapital) {
        ensureDataDirectoryExists();
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(ACCOUNT_FILE), StandardCharsets.UTF_8))) {
            writer.write(String.format("%.2f|%.2f", cashBalance, initialCapital));
            writer.newLine();
        } catch (IOException e) {
            System.err.println("[!] Error saving account data: " + e.getMessage());
        }
    }

    /**
     * Loads saved cash balance. If not found, returns initial virtual balance of 100,000.
     */
    public double loadCashBalance(double defaultBalance) {
        File file = new File(ACCOUNT_FILE);
        if (!file.exists()) {
            return defaultBalance;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                String[] parts = line.split("\\|");
                if (parts.length >= 1) {
                    return Double.parseDouble(parts[0].trim());
                }
            }
        } catch (Exception e) {
            System.err.println("[!] Warning: Could not read account file, using default balance.");
        }
        return defaultBalance;
    }

    // ==========================================
    // PORTFOLIO HOLDINGS PERSISTENCE
    // ==========================================

    /**
     * Saves portfolio holdings to file.
     */
    public void savePortfolio(Portfolio portfolio) {
        ensureDataDirectoryExists();
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(PORTFOLIO_FILE), StandardCharsets.UTF_8))) {
            for (Holding holding : portfolio.getAllHoldings()) {
                writer.write(holding.toDataString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("[!] Error saving portfolio data: " + e.getMessage());
        }
    }

    /**
     * Restores portfolio holdings from file.
     */
    public void loadPortfolio(Portfolio portfolio) {
        File file = new File(PORTFOLIO_FILE);
        if (!file.exists()) {
            return;
        }

        portfolio.clear();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Holding holding = Holding.fromDataString(line);
                if (holding != null) {
                    portfolio.putHolding(holding);
                }
            }
        } catch (IOException e) {
            System.err.println("[!] Error loading portfolio data: " + e.getMessage());
        }
    }

    // ==========================================
    // TRANSACTION HISTORY PERSISTENCE
    // ==========================================

    /**
     * Saves transaction history to file.
     */
    public void saveTransactions(List<Transaction> transactions) {
        ensureDataDirectoryExists();
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(TRANSACTIONS_FILE), StandardCharsets.UTF_8))) {
            for (Transaction t : transactions) {
                writer.write(t.toDataString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("[!] Error saving transactions: " + e.getMessage());
        }
    }

    /**
     * Appends a single transaction to the history file.
     */
    public void appendTransaction(Transaction transaction) {
        ensureDataDirectoryExists();
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(TRANSACTIONS_FILE, true), StandardCharsets.UTF_8))) {
            writer.write(transaction.toDataString());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("[!] Error recording transaction: " + e.getMessage());
        }
    }

    /**
     * Restores transaction history from file.
     */
    public List<Transaction> loadTransactions() {
        List<Transaction> list = new ArrayList<>();
        File file = new File(TRANSACTIONS_FILE);
        if (!file.exists()) {
            return list;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Transaction t = Transaction.fromDataString(line);
                if (t != null) {
                    list.add(t);
                }
            }
        } catch (IOException e) {
            System.err.println("[!] Error loading transaction history: " + e.getMessage());
        }
        return list;
    }

    // ==========================================
    // PERFORMANCE HISTORY PERSISTENCE
    // ==========================================

    /**
     * Saves all performance snapshots to file.
     */
    public void savePerformanceSnapshots(List<PerformanceSnapshot> snapshots) {
        ensureDataDirectoryExists();
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(PERFORMANCE_FILE), StandardCharsets.UTF_8))) {
            for (PerformanceSnapshot s : snapshots) {
                writer.write(s.toDataString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("[!] Error saving performance snapshots: " + e.getMessage());
        }
    }

    /**
     * Appends a single performance snapshot to the performance tracking file.
     */
    public void appendPerformanceSnapshot(PerformanceSnapshot snapshot) {
        ensureDataDirectoryExists();
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(PERFORMANCE_FILE, true), StandardCharsets.UTF_8))) {
            writer.write(snapshot.toDataString());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("[!] Error recording snapshot: " + e.getMessage());
        }
    }

    /**
     * Restores performance history snapshots from file.
     */
    public List<PerformanceSnapshot> loadPerformanceSnapshots() {
        List<PerformanceSnapshot> list = new ArrayList<>();
        File file = new File(PERFORMANCE_FILE);
        if (!file.exists()) {
            return list;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                PerformanceSnapshot s = PerformanceSnapshot.fromDataString(line);
                if (s != null) {
                    list.add(s);
                }
            }
        } catch (IOException e) {
            System.err.println("[!] Error loading performance history: " + e.getMessage());
        }
        return list;
    }

    // ==========================================
    // MARKET PRICES PERSISTENCE
    // ==========================================

    /**
     * Saves current simulated stock prices to file.
     */
    public void saveMarket(StockMarket market) {
        ensureDataDirectoryExists();
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(MARKET_FILE), StandardCharsets.UTF_8))) {
            for (Stock s : market.getAllStocks()) {
                writer.write(s.toDataString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("[!] Error saving market quotes: " + e.getMessage());
        }
    }

    /**
     * Restores market quotes from file if present.
     */
    public void loadMarket(StockMarket market) {
        File file = new File(MARKET_FILE);
        if (!file.exists()) {
            return;
        }

        List<Stock> loaded = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Stock s = Stock.fromDataString(line);
                if (s != null) {
                    loaded.add(s);
                }
            }
            if (!loaded.isEmpty()) {
                market.setStocks(loaded);
            }
        } catch (IOException e) {
            System.err.println("[!] Error loading market quotes: " + e.getMessage());
        }
    }

    // ==========================================
    // RESET UTILITY
    // ==========================================

    /**
     * Clears all saved files for a fresh start.
     */
    public void resetAllData() {
        String[] files = {ACCOUNT_FILE, PORTFOLIO_FILE, TRANSACTIONS_FILE, PERFORMANCE_FILE, MARKET_FILE};
        for (String filePath : files) {
            File f = new File(filePath);
            if (f.exists()) {
                f.delete();
            }
        }
    }
}
