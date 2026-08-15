package com.lopezinho.friendly_expense_tracker.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public class ChangePasswordRequest {
    private String currentPassword;

    @Size(min = 8, message = "The password must be at least eight characters long")
    private String newPassword;

    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}