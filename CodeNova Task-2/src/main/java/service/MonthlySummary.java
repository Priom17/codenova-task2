package service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class MonthlySummary {
    private final int year;
    private final int month;
    private final BigDecimal totalIncome;
    private final BigDecimal totalExpenses;
    private final BigDecimal netCashFlow;
    private final Map<String, BigDecimal> categoryExpenseTotals;
    private final Map<String, BigDecimal> categoryPercentages;

    public MonthlySummary(int year,
                          int month,
                          BigDecimal totalIncome,
                          BigDecimal totalExpenses,
                          BigDecimal netCashFlow,
                          Map<String, BigDecimal> categoryExpenseTotals,
                          Map<String, BigDecimal> categoryPercentages) {
        this.year = year;
        this.month = month;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.netCashFlow = netCashFlow;
        this.categoryExpenseTotals = Collections.unmodifiableMap(new LinkedHashMap<>(categoryExpenseTotals));
        this.categoryPercentages = Collections.unmodifiableMap(new LinkedHashMap<>(categoryPercentages));
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public BigDecimal getNetCashFlow() {
        return netCashFlow;
    }

    public Map<String, BigDecimal> getCategoryExpenseTotals() {
        return categoryExpenseTotals;
    }

    public Map<String, BigDecimal> getCategoryPercentages() {
        return categoryPercentages;
    }
}
