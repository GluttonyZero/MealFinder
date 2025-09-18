package com.mealquest.controller;

import com.mealquest.model.User;
import com.mealquest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/inventory")
@CrossOrigin(origins = "https://gluttonyzero.github.io/MealFinder")
public class InventoryController {

    @Autowired
    private UserRepository userRepository;

    // Get user inventory
    @GetMapping
    public ResponseEntity<List<String>> getInventory(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(user -> ResponseEntity.ok(user.getInventory()))
                .orElse(ResponseEntity.notFound().build());
    }

    // Add ingredient - FIXED to handle JSON strings
    @PostMapping("/add")
    public ResponseEntity<List<String>> addIngredient(@PathVariable Long userId, @RequestBody String ingredient) {
        // Remove quotes and trim the ingredient string
        String cleanIngredient = ingredient.replace("\"", "").trim();
        
        return userRepository.findById(userId)
                .map(user -> {
                    List<String> inv = user.getInventory();
                    if (!inv.contains(cleanIngredient)) {
                        inv.add(cleanIngredient);
                        user.setInventory(inv);
                        userRepository.save(user);
                    }
                    return ResponseEntity.ok(inv);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Remove ingredient - FIXED to handle JSON strings
    @PostMapping("/remove")
    public ResponseEntity<List<String>> removeIngredient(@PathVariable Long userId, @RequestBody String ingredient) {
        // Remove quotes and trim the ingredient string
        String cleanIngredient = ingredient.replace("\"", "").trim();
        
        return userRepository.findById(userId)
                .map(user -> {
                    List<String> inv = user.getInventory();
                    inv.remove(cleanIngredient);
                    user.setInventory(inv);
                    userRepository.save(user);
                    return ResponseEntity.ok(inv);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}