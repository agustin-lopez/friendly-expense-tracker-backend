package com.lopezinho.friendly_expense_tracker.controller;

import com.lopezinho.friendly_expense_tracker.dto.ResetPasswordRequest;
import com.lopezinho.friendly_expense_tracker.dto.ForgotPasswordRequest;
import com.lopezinho.friendly_expense_tracker.dto.LoginRequest;
import com.lopezinho.friendly_expense_tracker.dto.LoginResponse;
import com.lopezinho.friendly_expense_tracker.model.User;
import com.lopezinho.friendly_expense_tracker.service.JwtService;
import com.lopezinho.friendly_expense_tracker.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User saved = userService.register(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userService.findByEmail(request.getEmail());

        //NO USER-EMAIL MATCH WAS FOUND: 401
        if (userOpt.isEmpty()) return ResponseEntity.status(401).body("Wrong password/email");

        User user = userOpt.get();

        //GIVEN PASSWORD DOES NOT MATCH WITH HASHED PASSWORD: 401
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) { return ResponseEntity.status(401).body("Wrong password/email"); }

        //MATCH: GENERATE AND RETURN TOKEN
        String token = jwtService.generateToken(user.getEmail());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        userService.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "Recovery email sent successfully"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password updated successfully!"));
    }

}