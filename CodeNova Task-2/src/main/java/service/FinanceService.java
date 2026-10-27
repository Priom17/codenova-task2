package service;

import exception.InvalidTransactionException;
import model.Transaction;
import model.TransactionType;
import repository.TransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FinanceService {
    private static final int SCALE = 4;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    private final TransactionRepository repository;

    public FinanceService(TransactionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "Transaction repository cannot be null.");
    }

    public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }

    public Transaction addIncome(String category, BigDecimal amount, LocalDateTime timestamp, String remarks) {
        return addTransaction(TransactionType.INCOME, category, amount, timestamp, remarks);
    }

    public Transaction addExpense(String category, BigDecimal amount, LocalDateTime timestamp, String remarks) {
        return addTransaction(TransactionType.EXPENSE, category, amount, timestamp, remarks);
    }

    public List<Transaction> filterTransactions(LocalDate startDate, LocalDate endDate,
                                              String categoryFilter, TransactionType typeFilter) {
        TransactionValidator.validateDateRange(startDate, endDate);

        String normalizedCategory = categoryFilter == null ? null : categoryFilter.trim();
        List<Transaction> matches = new ArrayList<>();

        for (Transaction transaction : repository.findAll()) {
            LocalDate transactionDate = transaction.getTimestamp().toLocalDate();
            boolean matchesDate = (startDate == null || !transactionDate.isBefore(startDate))
                    && (endDate == null || !transactionDate.isAfter(endDate));
            boolean matchesCategory = normalizedCategory == null || normalizedCategory.isBlank()
                    || transaction.getCategory().equalsIgnoreCase(normalizedCategory);
            boolean matchesType = typeFilter == null || transaction.getType() == typeFilter;

            if (matchesDate && matchesCategory && matchesType) {
                matches.add(transaction);
            }
        }

        matches.sort(Comparator.comparing(Transaction::getTimestamp));
        return matches;
    }

    public MonthlySummary calculateMonthlySummary(int year, int month) {
        TransactionValidator.validateMonth(month);
        List<Transaction> monthlyTransactions = getTransactionsForMonth(year, month);

        BigDecimal totalIncome = monthlyTransactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = monthlyTransactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netCashFlow = totalIncome.subtract(totalExpenses);
        Map<String, BigDecimal> categoryExpenseTotals = new LinkedHashMap<>();
        for (Transaction transaction : monthlyTransactions) {
            if (transaction.getType() == TransactionType.EXPENSE) {
                categoryExpenseTotals.merge(transaction.getCategory(), transaction.getAmount(), BigDecimal::add);
            }
        }

        Map<String, BigDecimal> categoryPercentages = new LinkedHashMap<>();
        if (totalExpenses.compareTo(BigDecimal.ZERO) > 0) {
            for (Map.Entry<String, BigDecimal> entry : categoryExpenseTotals.entrySet()) {
                BigDecimal percent = entry.getValue()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(totalExpenses, 2, RoundingMode.HALF_UP);
                categoryPercentages.put(entry.getKey(), percent);
            }
        }

        return new MonthlySummary(year, month, roundMoney(totalIncome), roundMoney(totalExpenses), roundMoney(netCashFlow),
                categoryExpenseTotals.entrySet().stream()
                        .collect(LinkedHashMap::new, (map, entry) -> map.put(entry.getKey(), roundMoney(entry.getValue())), LinkedHashMap::putAll),
                categoryPercentages.entrySet().stream()
                        .collect(LinkedHashMap::new, (map, entry) -> map.put(entry.getKey(), roundMoneyPercent(entry.getValue())), LinkedHashMap::putAll));
    }

    public List<Transaction> getTransactionsForMonth(int year, int month) {
        TransactionValidator.validateMonth(month);
        List<Transaction> monthlyTransactions = new ArrayList<>();
        for (Transaction transaction : repository.findAll()) {
            if (transaction.getTimestamp().getYear() == year && transaction.getTimestamp().getMonthValue() == month) {
                monthlyTransactions.add(transaction);
            }
        }
        monthlyTransactions.sort(Comparator.comparing(Transaction::getTimestamp));
        return monthlyTransactions;
    }

    private Transaction addTransaction(TransactionType type, String category, BigDecimal amount, LocalDateTime timestamp, String remarks) {
        if (type == null) {
            throw new InvalidTransactionException("Transaction type is required.");
        }

        String validCategory = TransactionValidator.validateCategory(category);
        BigDecimal validAmount = TransactionValidator.parseAmount(amount == null ? "0" : amount.toPlainString());
        LocalDateTime validTimestamp = TransactionValidator.validateTimestamp(timestamp);

        Transaction transaction = new Transaction(type, validCategory, validAmount, validTimestamp, remarks);
        List<Transaction> allTransactions = repository.findAll();
        allTransactions.add(transaction);
        repository.saveAll(allTransactions);
        return transaction;
    }

    public static BigDecimal roundMoney(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal roundMoneyPercent(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
