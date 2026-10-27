package repository;

import model.Transaction;
import model.TransactionType;
import util.CsvUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CsvTransactionRepository implements TransactionRepository {
    private final Path storagePath;

    public CsvTransactionRepository(Path storagePath) {
        this.storagePath = storagePath.toAbsolutePath().normalize();
        ensureStorageFileExists();
    }

    @Override
    public List<Transaction> findAll() {
        if (!Files.exists(storagePath)) {
            return new ArrayList<>();
        }

        List<Transaction> transactions = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(storagePath, StandardCharsets.UTF_8)) {
            String line = reader.readLine();
            if (line == null) {
                return transactions;
            }

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                String[] columns = CsvUtils.parseCsvLine(line);
                if (columns.length != 6) {
                    System.err.println("Skipping malformed CSV record: " + line);
                    continue;
                }

                try {
                    Transaction transaction = parseTransaction(columns);
                    transactions.add(transaction);
                } catch (IllegalArgumentException e) {
                    System.err.println("Skipping unreadable transaction record: " + line + " (" + e.getMessage() + ")");
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read transaction data from " + storagePath, e);
        }

        return transactions;
    }

    @Override
    public void saveAll(List<Transaction> transactions) {
        try {
            Path parent = storagePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            List<String> rows = new ArrayList<>();
            rows.add("id,timestamp,type,category,amount,remarks");
            for (Transaction transaction : transactions) {
                rows.add(CsvUtils.toCsvRow(transaction));
            }

            try (BufferedWriter writer = Files.newBufferedWriter(storagePath, StandardCharsets.UTF_8)) {
                for (String row : rows) {
                    writer.write(row);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to save transactions to " + storagePath, e);
        }
    }

    private void ensureStorageFileExists() {
        try {
            Path parent = storagePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            if (!Files.exists(storagePath)) {
                Files.createFile(storagePath);
                try (BufferedWriter writer = Files.newBufferedWriter(storagePath, StandardCharsets.UTF_8)) {
                    writer.write("id,timestamp,type,category,amount,remarks");
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to initialize storage at " + storagePath, e);
        }
    }

    private Transaction parseTransaction(String[] columns) {
        String id = columns[0].trim();
        String timestampText = columns[1].trim();
        String typeText = columns[2].trim();
        String category = columns[3].trim();
        String amountText = columns[4].trim();
        String remarks = columns[5];

        LocalDateTime timestamp = LocalDateTime.parse(timestampText);
        TransactionType type = TransactionType.fromString(typeText);
        BigDecimal amount = new BigDecimal(amountText);

        return new Transaction(id, type, category, amount, timestamp, remarks);
    }
}
