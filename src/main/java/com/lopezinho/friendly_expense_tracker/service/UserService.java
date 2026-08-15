package com.lopezinho.friendly_expense_tracker.service;

import com.lopezinho.friendly_expense_tracker.exception.ExpiredTokenException;
import org.springframework.transaction.annotation.Transactional;
import com.lopezinho.friendly_expense_tracker.model.*;
import com.lopezinho.friendly_expense_tracker.repository.TemporaryTokenRepository;
import com.lopezinho.friendly_expense_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryService categoryService;
    private final TemporaryTokenRepository temporaryTokenRepository;
    private final EmailService emailService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, CategoryService categoryService, TemporaryTokenRepository temporaryTokenRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.categoryService = categoryService;
        this.temporaryTokenRepository = temporaryTokenRepository;
        this.emailService = emailService;
    }


    public List<User> findAll() { return userRepository.findAll(); }

    public Optional<User> findById(UUID id) { return userRepository.findById(id); }

    public Optional<User> findByEmail(String email) { return userRepository.findByEmail(email); }

    @Transactional
    public User register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) throw new RuntimeException("This email is already registered");

        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setEmailVerified(false);
        User savedUser = null;
        savedUser = userRepository.save(user);
        createDefaultCategories(savedUser);
        sendVerificationEmail(savedUser);
        return savedUser;
    }

    private void createDefaultCategories(User user) {
        List<Category> defaults = List.of(
                buildCategory("Food", CategoryType.EXPENSE, "PepsiThemeMyComputer", user),
                buildCategory("Transportation", CategoryType.EXPENSE, "VisualStudioPlane", user),
                buildCategory("Health", CategoryType.EXPENSE, "WindowsXPHearts", user),
                buildCategory("Entertainment", CategoryType.EXPENSE, "WindowsXPMyComputer2", user),
                buildCategory("Salary", CategoryType.INCOME, "BigMoneyDeluxe", user)
        );

        defaults.forEach(categoryService::save);
    }

    private Category buildCategory(String name, CategoryType type, String icon, User user) {
        Category category = new Category();
        category.setName(name);
        category.setType(type);
        category.setIcon(icon);
        category.setUser(user);
        return category;
    }

    public void requestPasswordChange(UUID id, String currentPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) throw new RuntimeException("Current password is wrong");

        String hashedNewPassword = passwordEncoder.encode(newPassword);
        String confirmLink = createToken(user, TokenType.PASSWORD_CHANGE_CONFIRMATION, 15, "/confirm-password-change", hashedNewPassword);

        emailService.sendPasswordChangeConfirmation(user.getEmail(), user.getName(), confirmLink);
    }

    public void confirmPasswordChange(String token) {
        TemporaryToken changeToken = validateToken(token, TokenType.PASSWORD_CHANGE_CONFIRMATION);

        User user = changeToken.getUser();
        user.setPasswordHash(changeToken.getPayload());
        userRepository.save(user);

        markTokenAsUsed(changeToken);
    }

    public void deleteById(UUID id) { userRepository.deleteById(id); }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    public User updateProfile(UUID id, String name) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setName(name);
        return userRepository.save(user);
    }

    public void requestPasswordReset(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return;

        User user = userOpt.get();
        String resetLink = createToken(user, TokenType.PASSWORD_RESET, 15, "/reset-password", null);
        emailService.sendPasswordResetEmail(user.getEmail(), user.getName(), resetLink);
    }

    public void resetPassword(String token, String newPassword) {
        TemporaryToken resetToken = validateToken(token, TokenType.PASSWORD_RESET);

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        markTokenAsUsed(resetToken);
    }

    private void sendVerificationEmail(User user) {
        String verificationLink = createToken(user, TokenType.EMAIL_VERIFICATION, 30, "/verify-email", null);
        emailService.sendVerificationEmail(user.getEmail(), user.getName(), verificationLink);
    }

    public void verifyEmail(String token) {
        TemporaryToken verificationToken = validateToken(token, TokenType.EMAIL_VERIFICATION);

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        markTokenAsUsed(verificationToken);
    }

    public void resendVerificationEmail(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            if (!user.isEmailVerified()) sendVerificationEmail(user);
        });
    }

    private String createToken(User user, TokenType type, int expirationMinutes, String frontendPath, String payload) {
        TemporaryToken temporaryToken = new TemporaryToken();
        temporaryToken.setUser(user);
        temporaryToken.setType(type);
        temporaryToken.setToken(UUID.randomUUID().toString());
        temporaryToken.setPayload(payload);
        temporaryToken.setExpiresAt(LocalDateTime.now().plusMinutes(expirationMinutes));
        temporaryTokenRepository.save(temporaryToken);

        return frontendUrl + frontendPath + "?token=" + temporaryToken.getToken();
    }

    private TemporaryToken validateToken(String token, TokenType expectedType) {
        TemporaryToken temporaryToken = temporaryTokenRepository.findByTokenAndType(token, expectedType)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        String email = temporaryToken.getUser().getEmail();

        if (temporaryToken.isUsed()) throw new ExpiredTokenException("This verification link has already been used", email);

        if (temporaryToken.getExpiresAt().isBefore(LocalDateTime.now())) throw new ExpiredTokenException("This verification link has already expired", email);

        return temporaryToken;
    }

    private void markTokenAsUsed(TemporaryToken token) {
        token.setUsed(true);
        temporaryTokenRepository.save(token);
    }
}