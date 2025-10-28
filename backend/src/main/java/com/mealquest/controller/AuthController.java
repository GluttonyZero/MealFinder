// AuthController.java
package com.mealquest.controller;

import com.mealquest.model.User;
import com.mealquest.dto.AuthResponse;
import com.mealquest.dto.LoginRequest;
import com.mealquest.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "https://gluttonyzero.github.io"})
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody User user) {
        System.out.println("Register endpoint called for user: " + user.getUsername());
        return authService.register(user);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest loginRequest) {
        System.out.println("Login endpoint called for user: " + loginRequest.getUsername());
        return authService.login(loginRequest);
    }
}