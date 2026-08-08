package com.lopezinho.friendly_expense_tracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "temporary_tokens")
public class TemporaryToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 255)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TokenType type;

    @Column(length = 255)
    private String payload;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean used = false;

    public TemporaryToken() {}

    public UUID getId() { return id; }

    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public String getToken() { return token; }

    public void setToken(String token) { this.token = token; }

    public TokenType getType() { return type; }

    public void setType(TokenType type) { this.type = type; }

    public String getPayload() { return payload; }

    public void setPayload(String payload) { this.payload = payload; }

    public LocalDateTime getExpiresAt() { return expiresAt; }

    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public boolean isUsed() { return used; }

    public void setUsed(boolean used) { this.used = used; }
}