package service;

import exception.InvalidTransactionException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class TransactionValidator {
    private TransactionValidator() {
    }

    public static BigDecimal parseAmount(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new InvalidTransactionException("Amount cannot be blank.");
        }

        try {
            BigDecimal amount = new BigDecimal(rawValue.trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidTransactionException("Amount must be greater than zero.");
            }
            return amount;
        } catch (NumberFormatException e) {
            throw new InvalidTransactionException("Invalid monetary input: " + rawValue);
        }
    }

    public static String validateCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new InvalidTransactionException("Category cannot be blank.");
        }
        return category.trim();
    }

    public static LocalDateTime validateTimestamp(LocalDateTime timestamp) {
        if (timestamp == null) {
            throw new InvalidTransactionException("Timestamp cannot be null.");
        }
        return timestamp;
    }

    public static LocalDate parseDate(String rawValue, String fieldName) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new InvalidTransactionException(fieldName + " cannot be blank.");
        }

        try {
            return LocalDate.parse(rawValue.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new InvalidTransactionException("Invalid " + fieldName + " format. Use yyyy-MM-dd.");
        }
    }

    public static void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new InvalidTransactionException("Start date cannot be later than the end date.");
        }
    }

    public static void validateMonth(int month) {
        if (month < 1 || month > 12) {
            throw new InvalidTransactionException("Month must be between 1 and 12.");
        }
    }
}
