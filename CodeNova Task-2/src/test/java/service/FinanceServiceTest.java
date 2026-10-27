package service;

import exception.InvalidTransactionException;
import model.Transaction;
import model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.CsvTransactionRepository;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FinanceServiceTest {
    private FinanceService service;
    private CsvTransactionRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        Path tempFile = Files.createTempFile("finance-test", ".csv");
        repository = new CsvTransactionRepository(tempFile);
        repository.saveAll(List.of());
        service = new FinanceService(repository);
    }

    @Test
    void addValidIncome() {
        Transaction transaction = service.addIncome("Salary", new BigDecimal("20000.00"), LocalDateTime.of(2026, 10, 15, 9, 0), "Monthly salary");

        assertNotNull(transaction);
        assertEquals(TransactionType.INCOME, transaction.getType());
        assertEquals("Salary", transaction.getCategory());
        assertEquals(new BigDecimal("20000.00"), transaction.getAmount());
    }

    @Test
    void rejectZeroAmount() {
        InvalidTransactionException exception = assertThrows(InvalidTransactionException.class,
                () -> service.addIncome("Salary", BigDecimal.ZERO, LocalDateTime.now(), "Bad input"));

        assertTrue(exception.getMessage().contains("greater than zero"));
    }

    @Test
    void calculateMonthlySummaryCorrectly() {
        service.addIncome("Salary", new BigDecimal("20000.00"), LocalDateTime.of(2026, 10, 1, 9, 0), "Salary");
        service.addExpense("Food", new BigDecimal("3000.00"), LocalDateTime.of(2026, 10, 5, 18, 0), "Groceries");
        service.addExpense("Travel", new BigDecimal("2000.00"), LocalDateTime.of(2026, 10, 12, 8, 0), "Train ticket");

        MonthlySummary summary = service.calculateMonthlySummary(2026, 10);

        assertEquals(new BigDecimal("20000.00"), summary.getTotalIncome());
        assertEquals(new BigDecimal("5000.00"), summary.getTotalExpenses());
        assertEquals(new BigDecimal("15000.00"), summary.getNetCashFlow());
        assertEquals(new BigDecimal("60.00"), summary.getCategoryPercentages().get("Food"));
        assertEquals(new BigDecimal("40.00"), summary.getCategoryPercentages().get("Travel"));
    }

    @Test
    void filterTransactionsByDateRangeAndCategory() {
        service.addIncome("Salary", new BigDecimal("5000.00"), LocalDateTime.of(2026, 10, 5, 9, 0), "Salary");
        service.addExpense("Food", new BigDecimal("250.00"), LocalDateTime.of(2026, 10, 7, 19, 0), "Lunch");
        service.addExpense("Travel", new BigDecimal("150.00"), LocalDateTime.of(2026, 11, 1, 11, 0), "Bus");

        List<Transaction> filtered = service.filterTransactions(LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31), "Food", TransactionType.EXPENSE);

        assertEquals(1, filtered.size());
        assertEquals("Food", filtered.get(0).getCategory());
    }

    @Test
    void zeroExpenseSummaryDoesNotThrowDivisionError() {
        service.addIncome("Salary", new BigDecimal("5000.00"), LocalDateTime.of(2026, 11, 1, 9, 0), "Salary");

        MonthlySummary summary = service.calculateMonthlySummary(2026, 11);

        assertEquals(BigDecimal.ZERO.setScale(2), summary.getTotalExpenses());
        assertTrue(summary.getCategoryPercentages().isEmpty());
    }
}
