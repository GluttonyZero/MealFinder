package com.mealquest.controller;

import com.mealquest.model.User;
import com.mealquest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users/{userId}/inventory")
@CrossOrigin(origins = "*") // Allow all origins temporarily for testing
public class InventoryController {

    @Autowired
    private UserRepository userRepository;

    // Get inventory
    @GetMapping
    public ResponseEntity<List<String>> getInventory(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(user -> ResponseEntity.ok(user.getInventory()))
                .orElse(ResponseEntity.notFound().build());
    }

    // Add ingredient
@PostMapping("/add")
public ResponseEntity<List<String>> addIngredient(
        @PathVariable Long userId,
        @RequestBody Object payload) {

    String ingredient = null;

    if (payload instanceof Map) {
        ingredient = (String) ((Map<?, ?>) payload).get("ingredient");
    } else if (payload instanceof String) {
        ingredient = (String) payload;
    }

    if (ingredient == null || ingredient.isBlank()) {
        return ResponseEntity.badRequest().build();
    }

    String cleanIngredient = ingredient.trim();

    return userRepository.findById(userId)
            .map(user -> {
                List<String> inv = user.getInventory();
                if (inv.stream().noneMatch(i -> i.equalsIgnoreCase(cleanIngredient))) {
                    inv.add(cleanIngredient);
                    user.setInventory(inv);
                    userRepository.save(user);
                }
                return ResponseEntity.ok(inv);
            })
            .orElse(ResponseEntity.notFound().build());
}

// Remove ingredient
@PostMapping("/remove")
public ResponseEntity<List<String>> removeIngredient(
        @PathVariable Long userId,
        @RequestBody Object payload) {

    String ingredient = null;

    if (payload instanceof Map) {
        ingredient = (String) ((Map<?, ?>) payload).get("ingredient");
    } else if (payload instanceof String) {
        ingredient = (String) payload;
    }

    if (ingredient == null || ingredient.isBlank()) {
        return ResponseEntity.badRequest().build();
    }

    String cleanIngredient = ingredient.trim();

    return userRepository.findById(userId)
            .map(user -> {
                List<String> inv = user.getInventory();
                inv.removeIf(i -> i.equalsIgnoreCase(cleanIngredient));
                user.setInventory(inv);
                userRepository.save(user);
                return ResponseEntity.ok(inv);
            })
            .orElse(ResponseEntity.notFound().build());
    }
}