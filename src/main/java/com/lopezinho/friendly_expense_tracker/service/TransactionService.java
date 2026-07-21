package com.lopezinho.friendly_expense_tracker.service;

import com.lopezinho.friendly_expense_tracker.model.Transaction;
import com.lopezinho.friendly_expense_tracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

}