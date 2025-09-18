package com.mealquest.service;

import com.mealquest.model.User;
import com.mealquest.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class InventoryService {
    private final UserRepository userRepository;

    public InventoryService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public List<String> addIngredient(Long userId, String ingredient) {
        // Clean the ingredient string
        String cleanIngredient = ingredient.replace("\"", "").trim();
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<String> inventory = user.getInventory();
        if (!inventory.contains(cleanIngredient)) {
            inventory.add(cleanIngredient);
            user.setInventory(inventory);
            userRepository.save(user);
        }
        return inventory;
    }

    @Transactional
    public List<String> removeIngredient(Long userId, String ingredient) {
        // Clean the ingredient string
        String cleanIngredient = ingredient.replace("\"", "").trim();
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<String> inventory = user.getInventory();
        inventory.remove(cleanIngredient);
        user.setInventory(inventory);
        userRepository.save(user);
        
        return inventory;
    }

    public List<String> getInventory(Long userId) {
        return userRepository.findById(userId)
                .map(User::getInventory)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}