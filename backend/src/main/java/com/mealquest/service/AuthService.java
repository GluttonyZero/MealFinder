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
        System.out.println("Registering user: " + user.getUsername());
        
        if (userRepository.existsByUsername(user.getUsername())) {
            return new AuthResponse("FAIL", "Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return new AuthResponse("FAIL", "Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        System.out.println("User registered successfully with ID: " + savedUser.getId());
        
        return new AuthResponse("SUCCESS", "Registration successful", savedUser.getId());
    }

    public AuthResponse login(LoginRequest loginRequest) {
        System.out.println("Logging in user: " + loginRequest.getUsername());
        
        Optional<User> user = userRepository.findByUsername(loginRequest.getUsername());
        
        if (user.isPresent() && passwordEncoder.matches(loginRequest.getPassword(), user.get().getPassword())) {
            System.out.println("Login successful for user: " + user.get().getUsername());
            return new AuthResponse("SUCCESS", "Login successful", user.get().getId());
        }
        
        System.out.println("Login failed for user: " + loginRequest.getUsername());
        return new AuthResponse("FAIL", "Invalid credentials");
    }
}