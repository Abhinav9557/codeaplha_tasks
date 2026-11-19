# Console-Based Stock Trading Platform
> **Java Programming Internship Project**  
> An educational, console-based stock trading simulation and real-time portfolio management application built exclusively with **Core Java (Java SE)**.

---

## 📌 Executive Summary

The **Console-Based Stock Trading Platform** is a complete, beginner-friendly simulation application designed to teach real-world stock market fundamentals—including buying, selling, weighted-average position calculation, market price fluctuations, and historical portfolio performance tracking over time.

### 🎯 Key Internship Constraints & Requirements Fulfilled
1. **Core Java Only**: Pure Java SE standard library. Zero external frameworks, zero Spring Boot, zero external databases, and zero live external APIs.
2. **Strict OOP Design**: Proper encapsulation, separation of concerns, abstraction, and modularity across model, service, controller, and persistence layers.
3. **Optimized Collections**: Utilizes `ArrayList` and `HashMap` appropriately for $O(1)$ fast lookups and formatted table iteration.
4. **Permanent File Handling**: Custom text-based storage engine using `BufferedReader` and `BufferedWriter` with `UTF-8` encoding to save and restore all user data across sessions.
5. **Virtual Capital & Safety**: Seeded with an initial virtual balance of **₹100,000.00**. Completely educational—does not connect to real financial markets or risk real money.

---

## 📂 Project Architecture

```
StockTradingPlatform/
│
├── Stock.java                 # Model: Stock symbol, company name, simulated price, fluctuations
├── Holding.java               # Model: Portfolio position tracking, weighted average buy price
├── Transaction.java           # Model: Immutable audit record of BUY/SELL trades
├── PerformanceSnapshot.java   # Model: Historical snapshot record for performance over time
│
├── StockMarket.java           # Service: Simulated market catalog, price quotes & tick fluctuations
├── Portfolio.java             # Service: Position management, valuation & financial analytics
├── DataManager.java           # Persistence: UTF-8 File I/O for saving & restoring disk data
├── TradingPlatform.java       # Controller: Business logic coordinator, validations & workflow
├── Main.java                  # Presentation: Interactive console UI, status bar & menu loop
│
├── run.bat                    # Windows Command Prompt 1-click build & launch script (UTF-8)
├── run.ps1                    # Windows PowerShell launcher script
├── README.md                  # Comprehensive project documentation & internship report
│
└── data/                      # Permanent Storage Directory (Auto-generated)
    ├── account.txt            # Current cash balance & starting capital
    ├── portfolio.txt          # Active stock holdings & weighted average prices
    ├── transactions.txt       # Chronological audit log of all completed trades
    ├── performance_history.txt# Historical snapshots of account valuation over time
    └── market.txt             # Latest simulated stock quotes
```

---

## 🏗️ Object-Oriented Programming (OOP) Principles

| OOP Principle | Implementation in Project |
| :--- | :--- |
| **Encapsulation** | All classes (`Stock`, `Holding`, `Transaction`, `PerformanceSnapshot`, etc.) use private fields with strictly validated public getters and setters. Mutable state cannot be modified directly from outside. |
| **Data Abstraction** | High-level controllers (`TradingPlatform`, `Main`) interact with clean interfaces like `portfolio.addShares()`, `market.refreshPrices()`, and `dataManager.saveAccount()` without worrying about underlying array mechanics or file byte streams. |
| **Single Responsibility** | Each class has one focused duty: `StockMarket` manages quotes, `Portfolio` calculates returns, `DataManager` manages disk files, and `Main` formats console output. |
| **Information Hiding** | Cost basis recalculation and file parsing logic are hidden within private methods and factory deserializers (`fromDataString`). |

---

## 📊 Data Structures Used

- **`HashMap<String, Stock>`**: Enables instantaneous $O(1)$ lookup of stocks by ticker symbol (e.g. `TCS`, `INFY`) during buy orders and portfolio valuation queries.
- **`HashMap<String, Holding>`**: Provides $O(1)$ access and updates to user holdings when adding or selling shares of an existing position.
- **`ArrayList<Stock>`**: Preserves display ordering for market catalog listings.
- **`ArrayList<Holding>`**: Enables tabular iteration over active portfolio positions.
- **`ArrayList<Transaction>`**: Maintains an ordered, chronological audit trail of all buy and sell orders.
- **`ArrayList<PerformanceSnapshot>`**: Stores timeline snapshots tracking account growth over time.

---

## 🧮 Financial Formulas & Calculations

### 1. Weighted Average Purchase Price (Buying Additional Shares)
When buying additional shares of an already-owned stock, the new cost basis per share is computed as:
$$\text{New Average Price} = \frac{(\text{Current Qty} \times \text{Old Avg Price}) + (\text{New Qty} \times \text{Purchase Price})}{\text{Current Qty} + \text{New Qty}}$$

### 2. Total Portfolio Market Value
$$\text{Total Portfolio Value} = \sum (\text{Holding Quantity} \times \text{Current Market Price})$$

### 3. Total Account Value
$$\text{Total Account Value} = \text{Available Cash Balance} + \text{Total Portfolio Value}$$

### 4. Overall Profit / Loss
$$\text{Profit or Loss} = \text{Total Account Value} - \text{Initial Investment Capital (₹100,000)}$$

### 5. Overall Profit / Loss Percentage
$$\text{Profit or Loss \%} = \left(\frac{\text{Profit or Loss}}{\text{Initial Investment Capital}}\right) \times 100$$

### 6. Realized Profit / Loss (Selling Shares)
$$\text{Realized P/L} = (\text{Selling Price} - \text{Average Purchase Price}) \times \text{Quantity Sold}$$

---

## 🚀 Step-by-Step Feature Walkthrough

### 1. Initial Virtual Balance
- Starts with **₹100,000.00** on first launch.
- If saved data exists in `data/account.txt`, restores the exact saved balance.
- Displayed prominently in the persistent status bar at the top of the menu.

### 2. Stock Market Catalog
Contains prominent simulated Indian equities:
| Symbol | Stock Name | Initial Simulated Price |
| :--- | :--- | :--- |
| **TCS** | Tata Consultancy Services | ₹3,500.00 |
| **INFY** | Infosys Limited | ₹1,500.00 |
| **RELIANCE** | Reliance Industries | ₹2,800.00 |
| **HDFCBANK** | HDFC Bank | ₹1,700.00 |
| **WIPRO** | Wipro Limited | ₹500.00 |
| **ICICIBANK** | ICICI Bank Limited | ₹1,100.00 |
| **ITC** | ITC Limited | ₹450.00 |
| **TATAMOTORS** | Tata Motors Limited | ₹950.00 |
| **SBIN** | State Bank of India | ₹800.00 |
| **BHARTIARTL** | Bharti Airtel Limited | ₹1,300.00 |

### 3. Buying Stocks
- Case-insensitive symbol entry (`tcs` or `TCS`).
- Comprehensive input validation: rejects negative quantities, non-integer inputs, and unlisted symbols.
- Checks available balance and prevents exceeding cash; informs user of the maximum affordable shares.
- Updates average price, deducts cash, logs transaction, triggers snapshot, and saves to disk.

### 4. Selling Stocks
- Displays currently owned stocks with quantity and live quotes.
- Verifies holding exists and requested quantity $\le$ owned quantity.
- Calculates proceeds, updates balance, reduces shares (removes position if quantity reaches 0).
- Computes realized profit/loss, records transaction, triggers snapshot, and saves to disk.

### 5. Simulated Market Volatility
- Menu option `5` triggers a market price fluctuation tick.
- Simulates realistic random price movements between **-5.0% and +5.0%**.
- **Safety Floor**: Stock prices are mathematically guaranteed to never drop to zero or negative (enforces a minimum ₹5.00 floor).
- Automatically recalculates portfolio values and takes a performance snapshot.

### 6. Historical Performance Tracking Over Time
Snapshots are permanently recorded upon:
1. Account initialization / reset
2. Successful BUY transactions
3. Successful SELL transactions
4. Market price refresh ticks

Each snapshot captures:
- Timestamp (`YYYY-MM-DD HH:MM:SS`)
- Trigger event description
- Available Cash Balance
- Portfolio Market Value
- Total Account Value
- Overall Profit / Loss (₹ and %)

---

## 💻 How to Compile and Run

### Prerequisites
- Java Development Kit (JDK 8, 11, 17, or 21+) installed.

### Option A: 1-Click Launchers (Windows)
- **Command Prompt**: Double-click or run `run.bat`
- **PowerShell**: Run `./run.ps1`

### Option B: Manual Command Line Execution
```bash
# 1. Navigate to the project root directory
cd StockTradingPlatform

# 2. Compile all Java source files with UTF-8 encoding
javac -encoding UTF-8 *.java

# 3. Run the application
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 Main
```

### Option C: In Any IDE (VS Code / IntelliJ IDEA / Eclipse)
1. Open the `StockTradingPlatform` folder in your IDE.
2. Locate `Main.java`.
3. Click **Run** or press `Shift + F10` (IntelliJ) / `F5` (VS Code).

---

## 🖥️ Sample Console Demonstration

```text
==========================================================================================
*                     CONSOLE-BASED STOCK TRADING PLATFORM                               *
*                   Core Java Simulation & Portfolio Tracker                             *
==========================================================================================
[+] Restored existing session data successfully.
[+] Current Available Cash Balance: ₹77,511.80
[+] Active Portfolio Holdings: 1
[+] Total Past Transactions: 2

+-----------------------------------------------------------------------------------------+
| CASH: ₹77511.80     | PORTFOLIO: ₹22535.40     | TOTAL: ₹100047.20    | P/L: +₹47.20 (+0.05%)    |
+-----------------------------------------------------------------------------------------+
================================== MAIN MENU ==================================
  1. View Stock Market Quotes
  2. Buy Stocks
  3. Sell Stocks
  4. View Portfolio & Profit / Loss Summary
  5. Refresh Market Prices (Simulate Market Fluctuations)
  6. View Transaction Audit History
  7. View Portfolio Performance Tracking Snapshots
  8. Reset Account Data (Start Fresh Virtual Simulation)
  9. Save & Exit Platform
===============================================================================
```

### Portfolio & Financial Summary Output
```text
+===================================================================================================================================+
|                                                        USER PORTFOLIO                                                             |
+------------+--------------------------------+-----+-------------+--------------+-------------+--------------+---------------------+
| Symbol     | Stock Name                     | Qty | Avg Price   | Total Cost   | Mkt Price   | Market Value | Profit / Loss (₹, %) |
+------------+--------------------------------+-----+-------------+--------------+-------------+--------------+---------------------+
| INFY       | Infosys Limited                |  15 | ₹   1500.00 | ₹   22500.00 | ₹   1502.36 | ₹   22535.40 | +₹35.40 (+0.16%)    |
+------------+--------------------------------+-----+-------------+--------------+-------------+--------------+---------------------+

+--------------------------------------------------------------------------------+
|                     FINANCIAL & PROFIT / LOSS SUMMARY                          |
+--------------------------------------------------------------------------------+
| Total Invested in Stocks (Cost Basis) : ₹       22,500.00                     |
| Current Portfolio Market Value        : ₹       22,535.40                     |
| Available Cash Balance                : ₹       77,511.80                     |
+--------------------------------------------------------------------------------+
| Total Account Value (Cash + Stocks)   : ₹      100,047.20                     |
| Initial Investment Capital            : ₹      100,000.00                     |
+--------------------------------------------------------------------------------+
| Overall Profit / Loss (PROFIT)        :         +₹47.20                       |
| Overall Profit / Loss Percentage      :          +0.05%                       |
+--------------------------------------------------------------------------------+
```

---

## 🛡️ Input Validation & Error Handling Checklist

- [x] **Non-numeric input for quantities**: Trapped with try-catch `NumberFormatException` without crashing.
- [x] **Negative & zero quantities**: Denied with clear user feedback.
- [x] **Exceeding cash balance**: Calculates maximum affordable shares and prevents transaction.
- [x] **Selling unowned stocks**: Prevented with clear warning.
- [x] **Selling more shares than owned**: Restricted with maximum allowable share limit.
- [x] **Invalid stock symbols**: Case-insensitive validation against market registry.
- [x] **File corruption safety**: Graceful fallback to default virtual capital if files are missing or empty.
- [x] **Reset safeguard**: Requires explicit confirmation (`RESET`) before clearing files.

---

## 👨‍💻 Author & Academic Attribution
- **Internship Project**: Java Programming Internship
- **Technology Stack**: Core Java (JDK 21 LTS), OOP, Collections (`ArrayList`, `HashMap`), Java File I/O (`BufferedReader`, `BufferedWriter`)
- **Version**: 1.0.0
