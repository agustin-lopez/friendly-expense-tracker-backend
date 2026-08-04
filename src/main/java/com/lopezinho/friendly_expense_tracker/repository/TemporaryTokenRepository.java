package com.lopezinho.friendly_expense_tracker.repository;

import com.lopezinho.friendly_expense_tracker.model.TemporaryToken;
import com.lopezinho.friendly_expense_tracker.model.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

import java.util.Optional;
import java.util.UUID;

public interface TemporaryTokenRepository extends JpaRepository<TemporaryToken, UUID> {
    Optional<TemporaryToken> findByTokenAndType(String token, TokenType type);

    @Modifying
    @Transactional
    @Query("DELETE FROM TemporaryToken t WHERE t.expiresAt < :now OR t.used = true")
    void deleteExpiredOrUsedTokens(LocalDateTime now);
}