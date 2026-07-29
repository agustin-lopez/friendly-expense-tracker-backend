package com.lopezinho.friendly_expense_tracker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class EmailService {

    @Value("${app.resend.api-key}")
    private String apiKey;

    @Value("${app.resend.from-email}")
    private String fromEmail;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String htmlBody = """
                <h2>Password recovery</h2>
                <p><a href="%s">Click here to set your new password</a></p>
                <p>This link expires in 15 minutes. Hurry up!</p>
                """.formatted(resetLink);

        Map<String, Object> body = Map.of(
                "from", fromEmail,
                "to", toEmail,
                "subject", "Friendly Expense Tracker - Password recovery",
                "html", htmlBody
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity("https://api.resend.com/emails", request, String.class);
    }
}