package com.lopezinho.friendly_expense_tracker.service;

import com.lopezinho.friendly_expense_tracker.repository.TemporaryTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class TokenCleanupService {

    private final TemporaryTokenRepository temporaryTokenRepository;

    public TokenCleanupService(TemporaryTokenRepository temporaryTokenRepository) {
        this.temporaryTokenRepository = temporaryTokenRepository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void cleanupExpiredTokens() { temporaryTokenRepository.deleteExpiredOrUsedTokens(LocalDateTime.now()); }
}