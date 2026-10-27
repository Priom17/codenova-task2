package service;

import model.Transaction;
import util.CsvUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FinanceReportService {
    public Path exportTransactionsCsv(List<Transaction> transactions, Path directory) {
        Path exportDirectory = directory.toAbsolutePath().normalize();
        try {
            Files.createDirectories(exportDirectory);
            Path exportPath = resolveUniqueOutputPath(exportDirectory, "transactions_export.csv");
            List<String> lines = new ArrayList<>();
            lines.add("id,timestamp,type,category,amount,remarks");
            for (Transaction transaction : transactions) {
                lines.add(CsvUtils.toCsvRow(transaction));
            }

            try (BufferedWriter writer = Files.newBufferedWriter(exportPath, StandardCharsets.UTF_8)) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            return exportPath;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to export transactions CSV file.", e);
        }
    }

    public Path exportMonthlySummaryCsv(MonthlySummary summary, Path directory) {
        Path exportDirectory = directory.toAbsolutePath().normalize();
        try {
            Files.createDirectories(exportDirectory);
            Path exportPath = resolveUniqueOutputPath(exportDirectory, "monthly_summary_" + summary.getYear() + "_" + summary.getMonth() + ".csv");

            List<String> lines = new ArrayList<>();
            lines.add("selected_month,selected_year,total_income,total_expenses,net_cash_flow");
            lines.add(String.join(",",
                    CsvUtils.escapeCsvField(String.valueOf(summary.getMonth())),
                    CsvUtils.escapeCsvField(String.valueOf(summary.getYear())),
                    CsvUtils.escapeCsvField(summary.getTotalIncome().toPlainString()),
                    CsvUtils.escapeCsvField(summary.getTotalExpenses().toPlainString()),
                    CsvUtils.escapeCsvField(summary.getNetCashFlow().toPlainString())));
            lines.add("category,expense_total,percentage");
            for (Map.Entry<String, BigDecimal> entry : summary.getCategoryExpenseTotals().entrySet()) {
                String category = entry.getKey();
                BigDecimal total = entry.getValue();
                BigDecimal percentage = summary.getCategoryPercentages().getOrDefault(category, BigDecimal.ZERO);
                lines.add(String.join(",",
                        CsvUtils.escapeCsvField(category),
                        CsvUtils.escapeCsvField(total.toPlainString()),
                        CsvUtils.escapeCsvField(percentage.toPlainString())));
            }

            try (BufferedWriter writer = Files.newBufferedWriter(exportPath, StandardCharsets.UTF_8)) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            return exportPath;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to export monthly summary CSV file.", e);
        }
    }

    public Path exportTextReport(MonthlySummary summary, Path directory) {
        Path exportDirectory = directory.toAbsolutePath().normalize();
        try {
            Files.createDirectories(exportDirectory);
            Path exportPath = resolveUniqueOutputPath(exportDirectory, "monthly_summary_report_" + summary.getYear() + "_" + summary.getMonth() + ".txt");
            String report = generateTextReport(summary);
            Files.writeString(exportPath, report, StandardCharsets.UTF_8);
            return exportPath;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write text report.", e);
        }
    }

    public String generateTextReport(MonthlySummary summary) {
        StringBuilder report = new StringBuilder();
        report.append("Monthly Financial Summary\n");
        report.append("Year: ").append(summary.getYear()).append("\n");
        report.append("Month: ").append(summary.getMonth()).append("\n");
        report.append("Total income: ").append(summary.getTotalIncome()).append("\n");
        report.append("Total expenses: ").append(summary.getTotalExpenses()).append("\n");
        report.append("Net cash flow: ").append(summary.getNetCashFlow()).append("\n\n");
        report.append("Category-wise spending\n");
        if (summary.getCategoryExpenseTotals().isEmpty()) {
            report.append("No expense categories recorded for this month.\n");
        } else {
            for (Map.Entry<String, BigDecimal> entry : summary.getCategoryExpenseTotals().entrySet()) {
                String category = entry.getKey();
                BigDecimal total = entry.getValue();
                BigDecimal percentage = summary.getCategoryPercentages().getOrDefault(category, BigDecimal.ZERO);
                report.append(String.format("- %s: %s (%s%%)\n", category, total, percentage));
            }
        }
        return report.toString();
    }

    private Path resolveUniqueOutputPath(Path directory, String fileName) {
        Path candidate = directory.resolve(fileName);
        int counter = 1;
        while (Files.exists(candidate)) {
            String name = fileName;
            int dot = name.lastIndexOf('.');
            String baseName = dot >= 0 ? name.substring(0, dot) : name;
            String extension = dot >= 0 ? name.substring(dot) : "";
            candidate = directory.resolve(baseName + "_" + counter + extension);
            counter++;
        }
        return candidate;
    }

    private String formatTransactionRow(Transaction transaction) {
        return String.join(",",
                transaction.getId(),
                transaction.getTimestamp().toString(),
                transaction.getType().name(),
                transaction.getCategory(),
                transaction.getAmount().toPlainString(),
                transaction.getRemarks().replace("\n", " ")
        );
    }
}
