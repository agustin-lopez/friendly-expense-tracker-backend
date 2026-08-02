package com.lopezinho.friendly_expense_tracker.repository;

import com.lopezinho.friendly_expense_tracker.model.TemporaryToken;
import com.lopezinho.friendly_expense_tracker.model.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface TemporaryTokenRepository extends JpaRepository<TemporaryToken, UUID> {
    Optional<TemporaryToken> findByTokenAndType(String token, TokenType type);
}