package com.lopezinho.friendly_expense_tracker.dto;

public class LoginResponse {

    private String token;

    public LoginResponse(String token) {
        this.token = token;
    }

    //GETTERS / SETTERS
    public String getToken() { return token; }

    public void setToken(String token) { this.token = token; }
}