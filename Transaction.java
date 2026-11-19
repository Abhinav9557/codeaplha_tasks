import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Transaction.java
 * Model class representing an executed trade (BUY or SELL).
 * 
 * Provides an immutable audit trail of each order with timestamp,
 * stock details, execution price, total consideration, and resulting cash balance.
 */
public class Transaction {
    private String transactionId;
    private String timestamp;
    private String type; // "BUY" or "SELL"
    private String symbol;
    private String stockName;
    private int quantity;
    private double pricePerShare;
    private double totalAmount;
    private double balanceAfter;

    /**
     * Constructs a new transaction with an auto-generated timestamp.
     */
    public Transaction(String transactionId, String type, String symbol, String stockName,
                       int quantity, double pricePerShare, double totalAmount, double balanceAfter) {
        this.transactionId = transactionId;
        this.timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.type = type.toUpperCase().trim();
        this.symbol = symbol.toUpperCase().trim();
        this.stockName = stockName.trim();
        this.quantity = quantity;
        this.pricePerShare = Math.round(pricePerShare * 100.0) / 100.0;
        this.totalAmount = Math.round(totalAmount * 100.0) / 100.0;
        this.balanceAfter = Math.round(balanceAfter * 100.0) / 100.0;
    }

    /**
     * Constructs a transaction restoring historical timestamp.
     */
    public Transaction(String transactionId, String timestamp, String type, String symbol,
                       String stockName, int quantity, double pricePerShare,
                       double totalAmount, double balanceAfter) {
        this.transactionId = transactionId;
        this.timestamp = timestamp;
        this.type = type.toUpperCase().trim();
        this.symbol = symbol.toUpperCase().trim();
        this.stockName = stockName.trim();
        this.quantity = quantity;
        this.pricePerShare = Math.round(pricePerShare * 100.0) / 100.0;
        this.totalAmount = Math.round(totalAmount * 100.0) / 100.0;
        this.balanceAfter = Math.round(balanceAfter * 100.0) / 100.0;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getStockName() {
        return stockName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPricePerShare() {
        return pricePerShare;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    /**
     * Serializes transaction to delimited text for file storage.
     */
    public String toDataString() {
        return transactionId + "|" + timestamp + "|" + type + "|" + symbol + "|" +
               stockName + "|" + quantity + "|" + pricePerShare + "|" + totalAmount + "|" + balanceAfter;
    }

    /**
     * Deserializes transaction from delimited text.
     */
    public static Transaction fromDataString(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        String[] parts = line.split("\\|");
        if (parts.length >= 9) {
            try {
                String id = parts[0].trim();
                String ts = parts[1].trim();
                String type = parts[2].trim();
                String symbol = parts[3].trim();
                String name = parts[4].trim();
                int qty = Integer.parseInt(parts[5].trim());
                double price = Double.parseDouble(parts[6].trim());
                double total = Double.parseDouble(parts[7].trim());
                double balance = Double.parseDouble(parts[8].trim());
                return new Transaction(id, ts, type, symbol, name, qty, price, total, balance);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
