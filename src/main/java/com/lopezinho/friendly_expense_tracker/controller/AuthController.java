package com.lopezinho.friendly_expense_tracker.controller;

import com.lopezinho.friendly_expense_tracker.dto.LoginRequest;
import com.lopezinho.friendly_expense_tracker.dto.LoginResponse;
import com.lopezinho.friendly_expense_tracker.model.User;
import com.lopezinho.friendly_expense_tracker.service.JwtService;
import com.lopezinho.friendly_expense_tracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
}