package com.lopezinho.friendly_expense_tracker.repository;

import com.lopezinho.friendly_expense_tracker.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByUserId(UUID userId);

    @Query(value = "SELECT DISTINCT to_char(t.transaction_date, 'YYYY-MM') " +
            "FROM transactions t WHERE t.user_id = :userId ORDER BY 1 DESC", nativeQuery = true)
    List<String> findDistinctMonthsByUserId(UUID userId);

    List<Transaction> findByUserIdAndTransactionDateBetween(UUID userId, LocalDate start, LocalDate end);

    @Query(value = "SELECT DISTINCT to_char(t.transaction_date, 'YYYY-MM') " +
            "FROM transactions t JOIN categories c ON t.category_id = c.id " +
            "WHERE t.user_id = :userId AND c.type = :type ORDER BY 1 DESC", nativeQuery = true)
    List<String> findDistinctMonthsByUserIdAndType(UUID userId, String type);

}