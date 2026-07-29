package com.lopezinho.friendly_expense_tracker.service;

import com.lopezinho.friendly_expense_tracker.model.Category;
import com.lopezinho.friendly_expense_tracker.model.CategoryType;
import com.lopezinho.friendly_expense_tracker.model.PasswordResetToken;
import com.lopezinho.friendly_expense_tracker.model.User;
import com.lopezinho.friendly_expense_tracker.repository.PasswordResetTokenRepository;
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
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, CategoryService categoryService, PasswordResetTokenRepository passwordResetTokenRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.categoryService = categoryService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
    }


    public List<User> findAll() { return userRepository.findAll(); }

    public Optional<User> findById(UUID id) { return userRepository.findById(id); }

    public Optional<User> findByEmail(String email) { return userRepository.findByEmail(email); }

    public User register(User user) {
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        User savedUser = userRepository.save(user);
        createDefaultCategories(savedUser);
        return savedUser;
    }

    private void createDefaultCategories(User user) {
        List<Category> defaults = List.of(
                buildCategory("Food", CategoryType.EXPENSE, user),
                buildCategory("Health", CategoryType.EXPENSE, user),
                buildCategory("Transportation", CategoryType.EXPENSE, user),
                buildCategory("Salary", CategoryType.INCOME, user)
        );

        defaults.forEach(categoryService::save);
    }

    private Category buildCategory(String name, CategoryType type, User user) {
        Category category = new Category();
        category.setName(name);
        category.setType(type);
        category.setUser(user);
        return category;
    }

    public User changePassword(UUID id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    public void deleteById(UUID id) { userRepository.deleteById(id); }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    public User updateProfile(UUID id, String name, String email) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    public void requestPasswordReset(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return;
        }

        User user = userOpt.get();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        passwordResetTokenRepository.save(resetToken);

        String resetLink = frontendUrl + "/reset-password?token=" + resetToken.getToken();
        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token!"));

        if (resetToken.isUsed()) {
            throw new RuntimeException("This link was already used!");
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("This link is already expired!");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }
}