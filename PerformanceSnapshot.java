import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * PerformanceSnapshot.java
 * Model class representing a historical snapshot of the portfolio's financial health.
 * 
 * Captures portfolio metrics at key events (buy, sell, price refresh) to enable
 * historical tracking and trend analysis over time.
 */
public class PerformanceSnapshot {
    private String snapshotId;
    private String timestamp;
    private String triggerEvent;
    private double cashBalance;
    private double portfolioValue;
    private double totalAccountValue;
    private double profitLoss;
    private double profitLossPercentage;

    /**
     * Constructs a snapshot with an auto-generated timestamp.
     */
    public PerformanceSnapshot(String snapshotId, String triggerEvent, double cashBalance,
                               double portfolioValue, double totalAccountValue,
                               double profitLoss, double profitLossPercentage) {
        this.snapshotId = snapshotId;
        this.timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.triggerEvent = triggerEvent;
        this.cashBalance = Math.round(cashBalance * 100.0) / 100.0;
        this.portfolioValue = Math.round(portfolioValue * 100.0) / 100.0;
        this.totalAccountValue = Math.round(totalAccountValue * 100.0) / 100.0;
        this.profitLoss = Math.round(profitLoss * 100.0) / 100.0;
        this.profitLossPercentage = Math.round(profitLossPercentage * 100.0) / 100.0;
    }

    /**
     * Constructs a snapshot restoring historical timestamp from file.
     */
    public PerformanceSnapshot(String snapshotId, String timestamp, String triggerEvent,
                               double cashBalance, double portfolioValue, double totalAccountValue,
                               double profitLoss, double profitLossPercentage) {
        this.snapshotId = snapshotId;
        this.timestamp = timestamp;
        this.triggerEvent = triggerEvent;
        this.cashBalance = Math.round(cashBalance * 100.0) / 100.0;
        this.portfolioValue = Math.round(portfolioValue * 100.0) / 100.0;
        this.totalAccountValue = Math.round(totalAccountValue * 100.0) / 100.0;
        this.profitLoss = Math.round(profitLoss * 100.0) / 100.0;
        this.profitLossPercentage = Math.round(profitLossPercentage * 100.0) / 100.0;
    }

    public String getSnapshotId() {
        return snapshotId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTriggerEvent() {
        return triggerEvent;
    }

    public double getCashBalance() {
        return cashBalance;
    }

    public double getPortfolioValue() {
        return portfolioValue;
    }

    public double getTotalAccountValue() {
        return totalAccountValue;
    }

    public double getProfitLoss() {
        return profitLoss;
    }

    public double getProfitLossPercentage() {
        return profitLossPercentage;
    }

    /**
     * Serializes snapshot to delimited string for file storage.
     */
    public String toDataString() {
        return snapshotId + "|" + timestamp + "|" + triggerEvent + "|" +
               cashBalance + "|" + portfolioValue + "|" + totalAccountValue + "|" +
               profitLoss + "|" + profitLossPercentage;
    }

    /**
     * Deserializes snapshot from delimited string.
     */
    public static PerformanceSnapshot fromDataString(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        String[] parts = line.split("\\|");
        if (parts.length >= 8) {
            try {
                String id = parts[0].trim();
                String ts = parts[1].trim();
                String event = parts[2].trim();
                double cash = Double.parseDouble(parts[3].trim());
                double pValue = Double.parseDouble(parts[4].trim());
                double total = Double.parseDouble(parts[5].trim());
                double pnl = Double.parseDouble(parts[6].trim());
                double pnlPct = Double.parseDouble(parts[7].trim());
                return new PerformanceSnapshot(id, ts, event, cash, pValue, total, pnl, pnlPct);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
