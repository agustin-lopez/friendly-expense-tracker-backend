package com.lopezinho.friendly_expense_tracker.dto;

import java.math.BigDecimal;

public class TransactionSummaryDTO {
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal balance;

    public TransactionSummaryDTO(BigDecimal totalIncome, BigDecimal totalExpenses, BigDecimal balance) {
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.balance = balance;
    }

    public BigDecimal getTotalIncome() { return totalIncome; }
    public BigDecimal getTotalExpenses() { return totalExpenses; }
    public BigDecimal getBalance() { return balance; }
}