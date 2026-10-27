package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Transaction {
    private final String id;
    private final TransactionType type;
    private final String category;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;
    private final String remarks;

    public Transaction(TransactionType type, String category, BigDecimal amount, LocalDateTime timestamp, String remarks) {
        this.id = generateId();
        this.type = Objects.requireNonNull(type, "Transaction type cannot be null.");
        this.category = category;
        this.amount = amount;
        this.timestamp = timestamp;
        this.remarks = remarks == null ? "" : remarks;
    }

    public Transaction(String id, TransactionType type, String category, BigDecimal amount, LocalDateTime timestamp, String remarks) {
        this.id = id;
        this.type = Objects.requireNonNull(type, "Transaction type cannot be null.");
        this.category = category;
        this.amount = amount;
        this.timestamp = timestamp;
        this.remarks = remarks == null ? "" : remarks;
    }

    public static String generateId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public String getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getRemarks() {
        return remarks;
    }

    @Override
    public String toString() {
        return id + " | " + timestamp + " | " + type + " | " + category + " | " + amount + " | " + remarks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id)
                && type == that.type
                && Objects.equals(category, that.category)
                && Objects.equals(amount, that.amount)
                && Objects.equals(timestamp, that.timestamp)
                && Objects.equals(remarks, that.remarks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, category, amount, timestamp, remarks);
    }
}
