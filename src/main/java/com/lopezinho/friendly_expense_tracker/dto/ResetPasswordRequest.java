package com.lopezinho.friendly_expense_tracker.dto;

import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {

    private String token;
    @Size(min = 8, message = "Password must be at least eight characters long")
    private String newPassword;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}