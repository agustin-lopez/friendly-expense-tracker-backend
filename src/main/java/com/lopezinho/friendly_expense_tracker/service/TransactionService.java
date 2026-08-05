package com.lopezinho.friendly_expense_tracker.service;

import com.lopezinho.friendly_expense_tracker.dto.CategoryTotalDTO;
import com.lopezinho.friendly_expense_tracker.dto.MonthGroupDTO;
import com.lopezinho.friendly_expense_tracker.dto.PaginatedMonthsDTO;
import com.lopezinho.friendly_expense_tracker.dto.TransactionSummaryDTO;
import com.lopezinho.friendly_expense_tracker.model.CategoryType;
import com.lopezinho.friendly_expense_tracker.model.Transaction;
import com.lopezinho.friendly_expense_tracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) { this.transactionRepository = transactionRepository; }

    public List<Transaction> findAll() { return transactionRepository.findAll(); }

    public List<Transaction> findByUserId(UUID userId) { return transactionRepository.findByUserId(userId); }

    public Optional<Transaction> findById(UUID id) { return transactionRepository.findById(id); }

    public Transaction save(Transaction transaction) { return transactionRepository.save(transaction); }

    public void deleteById(UUID id) { transactionRepository.deleteById(id); }

    public Transaction update(UUID id, Transaction updatedData) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        transaction.setCategory(updatedData.getCategory());
        transaction.setAmount(updatedData.getAmount());
        transaction.setDescription(updatedData.getDescription());
        transaction.setTransactionDate(updatedData.getTransactionDate());

        return transactionRepository.save(transaction);
    }

    public TransactionSummaryDTO getSummary(UUID userId) {
        List<Transaction> all = transactionRepository.findByUserId(userId);

        BigDecimal income = all.stream()
                .filter(t -> t.getCategory() != null && t.getCategory().getType() == CategoryType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expenses = all.stream()
                .filter(t -> t.getCategory() != null && t.getCategory().getType() == CategoryType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new TransactionSummaryDTO(income, expenses, income.subtract(expenses));
    }

    public List<CategoryTotalDTO> getExpensesByCategory(UUID userId) {
        List<Transaction> all = transactionRepository.findByUserId(userId);

        Map<String, BigDecimal> totals = new LinkedHashMap<>();
        all.stream()
                .filter(t -> t.getCategory() != null && t.getCategory().getType() == CategoryType.EXPENSE)
                .forEach(t -> totals.merge(t.getCategory().getName(), t.getAmount(), BigDecimal::add));

        return totals.entrySet().stream()
                .map(e -> new CategoryTotalDTO(e.getKey(), e.getValue()))
                .toList();
    }

    public PaginatedMonthsDTO getTransactionsGroupedByMonth(UUID userId, int page, int size, String typeFilter) {
        List<String> allMonths = (typeFilter == null || typeFilter.equals("ALL"))
                ? transactionRepository.findDistinctMonthsByUserId(userId)
                : transactionRepository.findDistinctMonthsByUserIdAndType(userId, typeFilter);

        int totalMonths = allMonths.size();
        int totalPages = (int) Math.ceil((double) totalMonths / size);

        int fromIndex = Math.min(page * size, totalMonths);
        int toIndex = Math.min(fromIndex + size, totalMonths);
        List<String> monthsInPage = allMonths.subList(fromIndex, toIndex);

        List<MonthGroupDTO> content = monthsInPage.stream()
                .map(monthKey -> buildMonthGroup(userId, monthKey, typeFilter))
                .toList();

        return new PaginatedMonthsDTO(content, page, totalPages, totalMonths);
    }

    private MonthGroupDTO buildMonthGroup(UUID userId, String monthKey, String typeFilter) {
        YearMonth yearMonth = YearMonth.parse(monthKey);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        List<Transaction> transactions = transactionRepository
                .findByUserIdAndTransactionDateBetween(userId, start, end);

        if (typeFilter != null && !typeFilter.equals("ALL")) {
            transactions = transactions.stream()
                    .filter(t -> t.getCategory() != null && t.getCategory().getType().name().equals(typeFilter))
                    .toList();
        }

        transactions = new java.util.ArrayList<>(transactions);
        transactions.sort((a, b) -> b.getTransactionDate().compareTo(a.getTransactionDate()));

        BigDecimal total = transactions.stream()
                .map(t -> t.getCategory() != null && t.getCategory().getType() == CategoryType.INCOME
                        ? t.getAmount()
                        : t.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new MonthGroupDTO(monthKey, total, transactions);
    }

}