package view;

import model.Transaction;
import org.jline.reader.LineReader;
import service.MonthlySummary;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class FinanceView {
    private final LineReader lineReader;

    public FinanceView(LineReader lineReader) {
        this.lineReader = lineReader;
    }

    public void showMenu() {
        System.out.println("\n===== Personal Finance Tracker =====");
        System.out.println("1. Add Income");
        System.out.println("2. Add Expense");
        System.out.println("3. View All Transactions");
        System.out.println("4. View Monthly Summary");
        System.out.println("5. Filter Transactions");
        System.out.println("6. Export Transaction History");
        System.out.println("7. Export Monthly Financial Report");
        System.out.println("8. Exit");
        System.out.print("Choose an option: ");
    }

    public int readMenuChoice() {
        String input = readLine("");
        try {
            int choice = Integer.parseInt(input.trim());
            if (choice >= 1 && choice <= 8) {
                return choice;
            }
            showMessage("Invalid menu selection. Please enter a number from 1 to 8.");
            return 0;
        } catch (NumberFormatException e) {
            showMessage("Invalid menu selection. Please enter a valid integer.");
            return 0;
        }
    }

    public String readRequiredText(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
            showMessage("This field cannot be blank.");
        }
    }

    public String readOptionalText(String prompt) {
        String value = readLine(prompt);
        return value == null ? "" : value.trim();
    }

    public BigDecimal readAmount(String prompt) {
        while (true) {
            String input = readLine(prompt);
            try {
                BigDecimal value = new BigDecimal(input.trim());
                if (value.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new NumberFormatException();
                }
                return value;
            } catch (NumberFormatException e) {
                showMessage("Invalid amount. Please enter a number greater than zero.");
            }
        }
    }

    public LocalDateTime readDateTime(String prompt) {
        while (true) {
            String input = readLine(prompt + " (yyyy-MM-dd HH:mm): ");
            try {
                return LocalDateTime.parse(input.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } catch (DateTimeParseException e) {
                showMessage("Invalid date/time format. Use yyyy-MM-dd HH:mm");
            }
        }
    }

    public LocalDate readDate(String prompt) {
        while (true) {
            String input = readLine(prompt + " (yyyy-MM-dd): ");
            try {
                return LocalDate.parse(input.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException e) {
                showMessage("Invalid date format. Use yyyy-MM-dd.");
            }
        }
    }

    private String readLine(String prompt) {
        return lineReader.readLine(prompt);
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void showTransactions(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            System.out.println("No transactions available.");
            return;
        }

        System.out.println("\nTransaction history");
        System.out.println("Total transactions: " + transactions.size());
        System.out.printf("%-12s %-18s %-8s %-15s %-12s %s%n",
                "ID", "Timestamp", "Type", "Category", "Amount", "Remarks");
        System.out.println("--------------------------------------------------------------------------");
        for (Transaction transaction : transactions) {
            System.out.printf("%-12s %-18s %-8s %-15s %-12s %s%n",
                    transaction.getId(),
                    transaction.getTimestamp(),
                    transaction.getType(),
                    transaction.getCategory(),
                    transaction.getAmount(),
                    transaction.getRemarks());
        }
    }

    public void showMonthlySummary(MonthlySummary summary) {
        System.out.println("\nMonthly Summary for " + summary.getMonth() + "/" + summary.getYear());
        System.out.println("Total income: " + summary.getTotalIncome());
        System.out.println("Total expenses: " + summary.getTotalExpenses());
        System.out.println("Net cash flow: " + summary.getNetCashFlow());

        if (summary.getCategoryExpenseTotals().isEmpty()) {
            System.out.println("No expense records for this month.");
            return;
        }

        System.out.println("\nCategory-wise spending:");
        for (var entry : summary.getCategoryExpenseTotals().entrySet()) {
            String category = entry.getKey();
            BigDecimal expenseTotal = entry.getValue();
            BigDecimal percentage = summary.getCategoryPercentages().getOrDefault(category, BigDecimal.ZERO);
            System.out.println(category + ": " + expenseTotal + " (" + percentage + "%)");
        }
    }
}
