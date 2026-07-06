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

}