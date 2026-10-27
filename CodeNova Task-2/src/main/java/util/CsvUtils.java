package util;

import model.Transaction;

import java.util.ArrayList;
import java.util.List;

public final class CsvUtils {
    private CsvUtils() {
    }

    public static String escapeCsvField(String value) {
        if (value == null) {
            return "";
        }

        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n") || escaped.contains("\r")) {
            return '"' + escaped + '"';
        }
        return escaped;
    }

    public static String[] parseCsvLine(String line) {
        if (line == null || line.isBlank()) {
            return new String[0];
        }

        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }

        values.add(current.toString());
        return values.toArray(new String[0]);
    }

    public static String toCsvRow(Transaction transaction) {
        return String.join(",",
                escapeCsvField(transaction.getId()),
                escapeCsvField(transaction.getTimestamp().toString()),
                escapeCsvField(transaction.getType().name()),
                escapeCsvField(transaction.getCategory()),
                escapeCsvField(transaction.getAmount().toPlainString()),
                escapeCsvField(transaction.getRemarks())
        );
    }
}
