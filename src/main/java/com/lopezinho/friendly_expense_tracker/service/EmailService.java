package com.lopezinho.friendly_expense_tracker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Value("${app.title.url}")
    private String titleLink;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendPasswordResetEmail(String toEmail, String userName, String resetLink) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("resetLink", resetLink);
        context.setVariable("imageLink", titleLink);

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

    public void sendVerificationEmail(String toEmail, String userName, String verificationLink) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("verificationLink", verificationLink);
        context.setVariable("imageLink", titleLink);

        String htmlBody = templateEngine.process("email-verification", context);

        Map<String, Object> body = Map.of(
                "from", fromEmail,
                "to", toEmail,
                "subject", "Friendly Expense Tracker - Email verification",
                "html", htmlBody
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity("https://api.resend.com/emails", request, String.class);
    }

    public void sendPasswordChangeConfirmation(String toEmail, String userName, String confirmLink) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("confirmLink", confirmLink);
        context.setVariable("imageLink", titleLink);

        String htmlBody = templateEngine.process("password-update", context);

        Map<String, Object> body = Map.of(
                "from", fromEmail,
                "to", toEmail,
                "subject", "Friendly Expense Tracker - Password update",
                "html", htmlBody
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity("https://api.resend.com/emails", request, String.class);
    }
}