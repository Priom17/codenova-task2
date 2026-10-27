# CodeNova Personal Finance Tracker

## Project Overview
This project is a beginner-friendly Java personal finance tracker built as part of the CodeNova Java Programming Internship, Batch 01. It allows users to record income and expenses, view transaction history, calculate monthly summaries, filter transactions, and export reports as CSV or plain text.

The application is built using Java 17 and follows a modular object-oriented structure with clear separation of concerns between model, repository, service, util, view, and controller classes.

## Features
- Add income transactions with validation
- Add expense transactions with validation
- View all transaction history
- Calculate monthly total income, expenses, and net cash flow
- Calculate category-wise spending totals and percentages
- Filter transactions by date range, category, and type
- Export full or filtered transaction history to CSV
- Export monthly summaries to CSV and text format
- Persistent storage through a CSV file named `transactions.csv`
- BigDecimal-based finance calculations with safe rounding
- Editable console input with Up/Down arrow history navigation

## Technology Stack
- Java 17
- Maven
- JUnit 5
- JLine for terminal line editing and input history
- Java NIO.2
- Java Time API
- Console-based user interface

## Project Structure
```text
CodeNova-Personal-Finance-Tracker/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── app/
│   │   │   │   └── Main.java
│   │   │   ├── controller/
│   │   │   │   └── FinanceController.java
│   │   │   ├── exception/
│   │   │   │   └── InvalidTransactionException.java
│   │   │   ├── model/
│   │   │   │   ├── Transaction.java
│   │   │   │   └── TransactionType.java
│   │   │   ├── repository/
│   │   │   │   ├── TransactionRepository.java
│   │   │   │   └── CsvTransactionRepository.java
│   │   │   ├── service/
│   │   │   │   ├── FinanceService.java
│   │   │   │   ├── FinanceReportService.java
│   │   │   │   ├── MonthlySummary.java
│   │   │   │   └── TransactionValidator.java
│   │   │   ├── util/
│   │   │   │   └── CsvUtils.java
│   │   │   └── view/
│   │   │       └── FinanceView.java
│   │   └── resources/
│   └── test/
│       └── java/
│           ├── service/
│           │   └── FinanceServiceTest.java
│           └── util/
│               └── CsvUtilsTest.java
├── .gitignore
├── pom.xml
├── transactions.csv
├── exports/
├── README.md
└── target/
```

## Architecture and Responsibilities
- `model`: transaction data and transaction type enum
- `repository`: CSV reading and writing logic
- `service`: validation and financial calculations
- `service.FinanceReportService`: summary export logic
- `view`: menu, prompts, and output display
- `controller`: input coordination and workflow control
- `util`: CSV escaping and parsing helpers
- `app.Main`: entry point

## Financial Formulas
- Net cash flow = total income - total expenses
- Category spending percentage = (expenses in category / total expenses) * 100

## BigDecimal and Rounding Policy
All monetary values are stored and calculated using `BigDecimal` instead of `float` or `double`. Rounding is applied with `RoundingMode.HALF_UP` and a two-decimal display policy for money and percentages.

## Date and Time Handling
The project uses `LocalDateTime` for transaction timestamps and `LocalDate` for date range filtering. The default storage format is ISO date-time with format `yyyy-MM-ddTHH:mm:ss`.

## CSV Storage Format
The application reads and writes `transactions.csv` using Java NIO.2 and CSV escaping rules. Each record stores:
- transaction ID
- timestamp
- type
- category
- amount
- remarks

## Export Functionality
The app can export:
- full transaction history to CSV
- filtered transaction history to CSV
- monthly summary to CSV
- monthly summary to text report

## Installation and Setup
1. Install Java 17 or later.
2. Install Maven.
3. Open a terminal in the project root.
4. Run the commands below.

### Editing Console Input
The console uses JLine instead of basic `Scanner` input. While entering a value, use the left/right arrow keys to move the cursor and Backspace/Delete to edit the current line. After pressing Enter and moving to another prompt, press Up/Down to recall a previous entry, edit it, and press Enter to submit it again. History is kept only for the current application run.

## Build and Execution Commands (Windows)
```powershell
java -version
mvn -v
mvn clean test
mvn clean package
java -jar target/CodeNova-Personal-Finance-Tracker-1.0.0.jar
```

## Example Application Session
```text
===== Personal Finance Tracker =====
1. Add Income
2. Add Expense
3. View All Transactions
4. View Monthly Summary
5. Filter Transactions
6. Export Transaction History
7. Export Monthly Financial Report
8. Exit
Choose an option: 1
Enter income category: Salary
Enter amount: 20000
Enter date and time (yyyy-MM-dd HH:mm): 2026-10-15 09:00
Enter remarks (optional): Monthly salary
Income added successfully. Transaction ID: TXN-XXXXXXX
```

## Sample Monthly Summary
```text
Monthly Summary for 10/2026
Total income: 32000.00
Total expenses: 12000.00
Net cash flow: 20000.00

Category-wise spending:
Food: 3000.00 (25.00%)
Travel: 2000.00 (16.67%)
Education: 1800.00 (15.00%)
```

## Testing Instructions
Run the automated unit tests with:
```powershell
mvn test
```

Build the runnable JAR and start the application:
```powershell
mvn clean package
java -jar target/CodeNova-Personal-Finance-Tracker-1.0.0.jar
```

The tests cover:
- valid income and expense entry
- invalid amount rejection
- zero-expense summary handling
- filter logic by date range and category
- CSV parsing with quotation escaping

## Screenshots
Use the following placeholders for screenshots:
- `docs/screenshots/main-menu.png`
- `docs/screenshots/monthly-summary.png`
- `docs/screenshots/export-report.png`

## GitHub Preparation
Repository name to use:
`CodeNova-Personal-Finance-Tracker`

Instructions:
1. Initialize a Git repository with `git init`
2. Add files with `git add .`
3. Commit with `git commit -m "Initial commit"`
4. Create a GitHub repository named `CodeNova-Personal-Finance-Tracker`
5. Push with `git branch -M main` and `git remote add origin <your-link>`
6. Use `git push -u origin main`

## Known Limitations
- This is a local console application without a database backend.
- It does not integrate with an external bank API or online service.
- CSV is used as a lightweight persistent storage method.

## Future Enhancements
- Add user authentication and multi-user support
- Add budget alerts and charts
- Add search by keyword and category sorting
- Add CSV import from external files
- Add a Swing or JavaFX desktop interface

## Author
Name: Priom karmakar
GitHub: https://github.com/Priom17

## Final Acceptance Checklist
- [x] Java 17 project structure created
- [x] Console finance tracker works locally
- [x] BigDecimal used for calculations
- [x] CSV persistence implemented
- [x] Monthly summary and filtering supported
- [x] Exports implemented
- [x] Unit tests included
- [x] README provided
- [x] Sample data included
- [x] Build instructions provided
