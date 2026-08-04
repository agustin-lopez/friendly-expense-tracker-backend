package com.lopezinho.friendly_expense_tracker.exception;

public class ExpiredTokenException extends RuntimeException {
    private final String email;

    public ExpiredTokenException(String message, String email) {
        super(message);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}