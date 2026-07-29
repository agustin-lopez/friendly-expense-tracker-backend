package com.lopezinho.friendly_expense_tracker.repository;

import com.lopezinho.friendly_expense_tracker.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, java.util.UUID> {
    Optional<PasswordResetToken> findByToken(String token);
}