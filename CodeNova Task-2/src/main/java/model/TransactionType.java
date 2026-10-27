package model;

public enum TransactionType {
    INCOME,
    EXPENSE;

    public static TransactionType fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Transaction type cannot be null.");
        }

        return switch (value.trim().toUpperCase()) {
            case "INCOME" -> INCOME;
            case "EXPENSE" -> EXPENSE;
            default -> throw new IllegalArgumentException("Unsupported transaction type: " + value);
        };
    }
}
