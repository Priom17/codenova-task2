package controller;

import exception.InvalidTransactionException;
import model.Transaction;
import model.TransactionType;
import service.FinanceReportService;
import service.FinanceService;
import service.MonthlySummary;
import service.TransactionValidator;
import view.FinanceView;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class FinanceController {
    private final FinanceService financeService;
    private final FinanceReportService financeReportService;
    private final FinanceView view;

    public FinanceController(FinanceService financeService, FinanceReportService financeReportService, FinanceView view) {
        this.financeService = financeService;
        this.financeReportService = financeReportService;
        this.view = view;
    }

    public void run() {
        boolean running = true;

        while (running) {
            view.showMenu();
            int choice = view.readMenuChoice();

            try {
                switch (choice) {
                    case 1 -> addIncome();
                    case 2 -> addExpense();
                    case 3 -> viewAllTransactions();
                    case 4 -> showMonthlySummary();
                    case 5 -> filterTransactions();
                    case 6 -> exportHistory();
                    case 7 -> exportMonthlyReport();
                    case 8 -> {
                        view.showMessage("Thank you for using the Personal Finance Tracker. Goodbye!");
                        running = false;
                    }
                    default -> view.showMessage("Please choose a valid option.");
                }
            } catch (IllegalArgumentException e) {
                view.showMessage("Error: " + e.getMessage());
            } catch (Exception e) {
                view.showMessage("Unexpected error: " + e.getMessage());
            }
        }
    }

    private void addIncome() {
        String category = view.readRequiredText("Enter income category: ");
        BigDecimal amount = view.readAmount("Enter amount: ");
        LocalDateTime timestamp = view.readDateTime("Enter date and time");
        String remarks = view.readOptionalText("Enter remarks (optional): ");

        Transaction transaction = financeService.addIncome(category, amount, timestamp, remarks);
        view.showMessage("Income added successfully. Transaction ID: " + transaction.getId());
    }

    private void addExpense() {
        String category = view.readRequiredText("Enter expense category: ");
        BigDecimal amount = view.readAmount("Enter amount: ");
        LocalDateTime timestamp = view.readDateTime("Enter date and time");
        String remarks = view.readOptionalText("Enter remarks (optional): ");

        Transaction transaction = financeService.addExpense(category, amount, timestamp, remarks);
        view.showMessage("Expense added successfully. Transaction ID: " + transaction.getId());
    }

    private void viewAllTransactions() {
        List<Transaction> transactions = financeService.getAllTransactions();
        if (transactions.isEmpty()) {
            view.showMessage("No transactions found. The transaction history is empty.");
            return;
        }
        view.showTransactions(transactions);
    }

    private void showMonthlySummary() {
        int year = readYear();
        int month = readMonth();
        MonthlySummary summary = financeService.calculateMonthlySummary(year, month);
        view.showMonthlySummary(summary);
    }

    private void filterTransactions() {
        LocalDate startDate = null;
        LocalDate endDate = null;

        String startInput = view.readOptionalText("Start date (yyyy-MM-dd, blank for all): ");
        if (!startInput.isBlank()) {
            startDate = TransactionValidator.parseDate(startInput, "Start date");
        }

        String endInput = view.readOptionalText("End date (yyyy-MM-dd, blank for all): ");
        if (!endInput.isBlank()) {
            endDate = TransactionValidator.parseDate(endInput, "End date");
        }

        TransactionValidator.validateDateRange(startDate, endDate);

        String category = view.readOptionalText("Category (blank for all): ");
        String typeInput = view.readOptionalText("Type (INCOME/EXPENSE, blank for all): ");
        TransactionType type = null;
        if (!typeInput.isBlank()) {
            type = TransactionType.fromString(typeInput);
        }

        List<Transaction> filteredTransactions = financeService.filterTransactions(startDate, endDate, category, type);
        if (filteredTransactions.isEmpty()) {
            view.showMessage("No transactions match the selected filters.");
            return;
        }
        view.showTransactions(filteredTransactions);
    }

    private void exportHistory() {
        List<Transaction> allTransactions = financeService.getAllTransactions();
        Path exportPath = financeReportService.exportTransactionsCsv(allTransactions, Path.of("exports"));
        view.showMessage("Exported all transactions to: " + exportPath);
    }

    private void exportMonthlyReport() {
        int year = readYear();
        int month = readMonth();
        MonthlySummary summary = financeService.calculateMonthlySummary(year, month);
        Path csvPath = financeReportService.exportMonthlySummaryCsv(summary, Path.of("exports"));
        Path textPath = financeReportService.exportTextReport(summary, Path.of("exports"));
        view.showMessage("Monthly summary CSV exported to: " + csvPath);
        view.showMessage("Monthly summary text report exported to: " + textPath);
    }

    private int readMonth() {
        while (true) {
            String input = view.readOptionalText("Enter month (1-12): ");
            if (input.isBlank()) {
                view.showMessage("Month is required.");
                continue;
            }
            try {
                int month = Integer.parseInt(input.trim());
                TransactionValidator.validateMonth(month);
                return month;
            } catch (NumberFormatException | InvalidTransactionException e) {
                view.showMessage("Invalid month. Please enter a number from 1 to 12.");
            }
        }
    }

    private int readYear() {
        while (true) {
            String input = view.readOptionalText("Enter year: ");
            if (input.isBlank()) {
                view.showMessage("Year is required.");
                continue;
            }
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                view.showMessage("Invalid year. Please enter a valid integer year.");
            }
        }
    }
}
