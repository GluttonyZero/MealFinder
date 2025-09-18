package com.mealquest.service;

import com.mealquest.model.User;
import com.mealquest.repository.UserRepository;
import com.mealquest.dto.AuthResponse;
import com.mealquest.dto.LoginRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return new AuthResponse("FAIL", "Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return new AuthResponse("FAIL", "Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return new AuthResponse("SUCCESS", "Registration successful", savedUser.getId());
    }

    public AuthResponse login(LoginRequest loginRequest) {
        Optional<User> user = userRepository.findByUsername(loginRequest.getUsername());
        
        if (user.isPresent() && passwordEncoder.matches(loginRequest.getPassword(), user.get().getPassword())) {
            return new AuthResponse("SUCCESS", "Login successful", user.get().getId());
        }
        return new AuthResponse("FAIL", "Invalid credentials");
    }
}