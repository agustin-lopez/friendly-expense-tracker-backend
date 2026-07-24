package com.lopezinho.friendly_expense_tracker.dto;

import com.lopezinho.friendly_expense_tracker.model.Transaction;
import java.math.BigDecimal;
import java.util.List;

public class MonthGroupDTO {
    private String month;
    private BigDecimal total;
    private List<Transaction> transactions;

    public MonthGroupDTO(String month, BigDecimal total, List<Transaction> transactions) {
        this.month = month;
        this.total = total;
        this.transactions = transactions;
    }

    public String getMonth() { return month; }
    public BigDecimal getTotal() { return total; }
    public List<Transaction> getTransactions() { return transactions; }
}