package com.lopezinho.friendly_expense_tracker.service;

import com.lopezinho.friendly_expense_tracker.model.User;
import com.lopezinho.friendly_expense_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) { this.userRepository = userRepository; }

    public List<User> findAll() { return userRepository.findAll(); }

    public Optional<User> findById(UUID id) { return userRepository.findById(id); }

    public Optional<User> findByEmail(String email) { return userRepository.findByEmail(email); }

    public User save(User user) { return userRepository.save(user); }

    public void deleteById(UUID id) { userRepository.deleteById(id); }

}