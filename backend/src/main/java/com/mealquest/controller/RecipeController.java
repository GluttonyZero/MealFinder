package com.mealquest.controller;

import com.mealquest.model.Recipe;
import com.mealquest.model.User;
import com.mealquest.repository.UserRepository;
import com.mealquest.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;

@RestController
@RequestMapping("/api/recipes")
@CrossOrigin(origins = {"http://localhost:3000", "https://gluttonyzero.github.io", "https://mealfinder-0tmr.onrender.com"})
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/from-inventory/{userId}")
    public ResponseEntity<?> getRecipesFromInventory(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "User not found"));
        }

        List<String> inventory = user.getInventory();
        if (inventory == null || inventory.isEmpty()) {
            return ResponseEntity.ok(Map.of("status", "info", "message", "Inventory is empty"));
        }

        Map<String, Object> result = recipeService.getRecipeSuggestionsWithScoring(userId, inventory);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public List<Recipe> getAllRecipes() {
        return recipeService.getAllRecipes();
    }
    

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Long id) {
        return recipeService.getRecipeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Recipe createRecipe(@RequestBody Recipe recipe) {
        return recipeService.saveRecipe(recipe);
    }

    @GetMapping("/search")
    public List<Recipe> searchRecipes(@RequestParam String query) {
        return recipeService.searchRecipes(query);
    }

    @GetMapping("/category/{category}")
    public List<Recipe> getRecipesByCategory(@PathVariable String category) {
        return recipeService.getRecipesByCategory(category);
    }

    // Debug endpoint to see what's in the database
    @GetMapping("/debug")
    public Map<String, Object> debugRecipes() {
        Map<String, Object> debugInfo = new HashMap<>();
        
        try {
            List<Recipe> allRecipes = recipeService.getAllRecipes();
            debugInfo.put("totalRecipes", allRecipes.size());
            
            List<Map<String, Object>> recipeDetails = new ArrayList<>();
            for (Recipe recipe : allRecipes) {
                Map<String, Object> details = new HashMap<>();
                details.put("id", recipe.getId());
                details.put("name", recipe.getRecipeName());
                details.put("ingredients", recipe.getIngredients());
                details.put("ingredients_length", recipe.getIngredients() != null ? recipe.getIngredients().length() : 0);
                details.put("ingredients_preview", recipe.getIngredients() != null ? 
                    recipe.getIngredients().substring(0, Math.min(200, recipe.getIngredients().length())) + "..." : "null");
                recipeDetails.add(details);
            }
            
            debugInfo.put("recipes", recipeDetails);
            debugInfo.put("status", "success");
            
        } catch (Exception e) {
            debugInfo.put("status", "error");
            debugInfo.put("error", e.getMessage());
        }
        
        return debugInfo;
    }

    // Simple test endpoint
    @GetMapping("/test-match")
    public Map<String, Object> testMatching() {
        Map<String, Object> result = new HashMap<>();
        
        // Test with hardcoded ingredients
        List<String> testInventory = List.of("apple", "sugar", "butter");
        List<Recipe> allRecipes = recipeService.getAllRecipes();
        
        result.put("testInventory", testInventory);
        result.put("totalRecipes", allRecipes.size());
        
        List<Map<String, Object>> matches = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            int matchCount = 0;
            List<String> matched = new ArrayList<>();
            
            if (recipe.getIngredients() != null) {
                String ingredients = recipe.getIngredients().toLowerCase();
                for (String ing : testInventory) {
                    if (ingredients.contains(ing.toLowerCase())) {
                        matchCount++;
                        matched.add(ing);
                    }
                }
            }
            
            if (matchCount > 0) {
                Map<String, Object> match = new HashMap<>();
                match.put("recipeName", recipe.getRecipeName());
                match.put("matches", matchCount);
                match.put("matchedIngredients", matched);
                match.put("allIngredients", recipe.getIngredients());
                matches.add(match);
            }
        }
        
        result.put("matchingRecipes", matches);
        return result;
    }
}