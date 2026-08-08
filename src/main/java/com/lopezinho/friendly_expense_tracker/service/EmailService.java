package com.lopezinho.friendly_expense_tracker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
public class EmailService {

    @Value("${app.resend.api-key}")
    private String apiKey;

    @Value("${app.resend.from-email}")
    private String fromEmail;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private TemplateEngine templateEngine;

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Context context = new Context();
        //context.setVariable("userName", userName);
        context.setVariable("resetLink", resetLink);

        String htmlBody = templateEngine.process("password-recovery", context);


        Map<String, Object> body = Map.of(
                "from", fromEmail,
                "to", toEmail,
                "subject", "Friendly Expense Tracker - Password recovery",
                "html", htmlBody
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity("https://api.resend.com/emails", request, String.class);
    }

    public void sendVerificationEmail(String toEmail, String verificationLink) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String htmlBody = """
            <h2>Email verification</h2>
            <p>Thank you for signing up!</p>
            <p><a href="%s">Click this link to activate your account</a></p>
            <p>It expires in 30 minutes.</p>
            """.formatted(verificationLink);

        Map<String, Object> body = Map.of(
                "from", fromEmail,
                "to", toEmail,
                "subject", "Friendly Expense Tracker - Email verification",
                "html", htmlBody
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity("https://api.resend.com/emails", request, String.class);
    }

    public void sendPasswordChangeConfirmation(String toEmail, String confirmLink) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String htmlBody = """
            <h2>Password update confirmation</h2>
            <p>You have requested to update your password.</p>
            <p><a href="%s">Click here to confirm!</a></p>
            <p>This link expires in 15 minutes.
            If you haven't requested this, please consider change your current password for a safer one.</p>
            """.formatted(confirmLink);

        Map<String, Object> body = Map.of(
                "from", fromEmail,
                "to", toEmail,
                "subject", "Friendly Expense Tracker - Password update confirmation",
                "html", htmlBody
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity("https://api.resend.com/emails", request, String.class);
    }
}