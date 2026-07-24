package com.lopezinho.friendly_expense_tracker.controller;

import com.lopezinho.friendly_expense_tracker.dto.CategoryTotalDTO;
import com.lopezinho.friendly_expense_tracker.dto.PaginatedMonthsDTO;
import com.lopezinho.friendly_expense_tracker.dto.TransactionSummaryDTO;
import com.lopezinho.friendly_expense_tracker.model.Transaction;
import com.lopezinho.friendly_expense_tracker.model.User;
import com.lopezinho.friendly_expense_tracker.service.TransactionService;
import com.lopezinho.friendly_expense_tracker.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }


    @GetMapping
    public List<Transaction> findAll() {
        //GET CURRENT AUTHENTICATED USER
        User currentUser = userService.getCurrentUser();
        return transactionService.findByUserId(currentUser.getId());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> findById(@PathVariable UUID id) {
        return transactionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Transaction> create(@RequestBody Transaction transaction) {
        //GET CURRENT AUTHENTICATED USER
        User currentUser = userService.getCurrentUser();
        //REPLACE USER IN REQUEST BODY
        transaction.setUser(currentUser);
        Transaction saved = transactionService.save(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        transactionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> update(@PathVariable UUID id, @RequestBody Transaction transaction) {
        Transaction updated = transactionService.update(id, transaction);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/summary")
    public TransactionSummaryDTO getSummary() {
        User currentUser = userService.getCurrentUser();
        return transactionService.getSummary(currentUser.getId());
    }

    @GetMapping("/by-category")
    public List<CategoryTotalDTO> getByCategory() {
        User currentUser = userService.getCurrentUser();
        return transactionService.getExpensesByCategory(currentUser.getId());
    }

    @GetMapping("/grouped")
    public PaginatedMonthsDTO getGrouped(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size,
            @RequestParam(required = false) String type
    ) {
        User currentUser = userService.getCurrentUser();
        return transactionService.getTransactionsGroupedByMonth(currentUser.getId(), page, size, type);
    }

}