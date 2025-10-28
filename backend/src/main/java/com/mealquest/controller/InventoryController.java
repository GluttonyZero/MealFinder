package com.mealquest.controller;

import com.mealquest.model.User;
import com.mealquest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users/{userId}/inventory")
@CrossOrigin(origins = {"http://localhost:3000", "https://gluttonyzero.github.io", "https://mealfinder-0tmr.onrender.com"})
public class InventoryController {

    @Autowired
    private UserRepository userRepository;

    // Get inventory
    @GetMapping
    public ResponseEntity<List<String>> getInventory(@PathVariable Long userId) {
        System.out.println("Getting inventory for user: " + userId);
        return userRepository.findById(userId)
                .map(user -> {
                    List<String> inventory = user.getInventory();
                    System.out.println("Found inventory: " + inventory);
                    return ResponseEntity.<List<String>>ok(new ArrayList<>(inventory));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Add ingredient
    @PostMapping("/add")
    public ResponseEntity<List<String>> addIngredient(@PathVariable Long userId, @RequestBody Map<String, String> payload) {
        System.out.println("Adding ingredient for user: " + userId + ", payload: " + payload);
        String ingredient = payload.get("ingredient");
        if (ingredient == null || ingredient.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return userRepository.findById(userId)
                .map(user -> {
                    String cleanIngredient = ingredient.trim();
                    user.addToInventory(cleanIngredient);
                    userRepository.save(user);
                    
                    List<String> updatedInventory = user.getInventory();
                    System.out.println("Added ingredient: " + cleanIngredient + ", new inventory: " + updatedInventory);
                    return ResponseEntity.<List<String>>ok(new ArrayList<>(updatedInventory));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Remove ingredient
    @PostMapping("/remove")
    public ResponseEntity<List<String>> removeIngredient(@PathVariable Long userId, @RequestBody Map<String, String> payload) {
        System.out.println("Removing ingredient for user: " + userId + ", payload: " + payload);
        String ingredient = payload.get("ingredient");
        if (ingredient == null || ingredient.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return userRepository.findById(userId)
                .map(user -> {
                    String cleanIngredient = ingredient.trim();
                    user.removeFromInventory(cleanIngredient);
                    userRepository.save(user);
                    
                    List<String> updatedInventory = user.getInventory();
                    System.out.println("Removed ingredient: " + cleanIngredient + ", new inventory: " + updatedInventory);
                    return ResponseEntity.<List<String>>ok(new ArrayList<>(updatedInventory));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}