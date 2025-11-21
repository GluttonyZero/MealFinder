package com.mealquest.service;

import com.mealquest.model.Recipe;
import com.mealquest.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public Map<String, Object> getRecipeSuggestionsWithScoring(Long userId, List<String> userInventory) {
        System.out.println("=== MEAL CHALLENGES DEBUG START ===");
        System.out.println("User ID: " + userId);
        System.out.println("User Inventory: " + userInventory);
        
        Map<String, Object> result = new HashMap<>();
        
        if (userInventory == null || userInventory.isEmpty()) {
            System.out.println("❌ User inventory is empty");
            result.put("status", "info");
            result.put("message", "Inventory is empty");
            result.put("recipes", Collections.emptyList());
            result.put("userInventorySize", 0);
            result.put("totalRecipesFound", 0);
            return result;
        }

        // Get ALL recipes
        List<Recipe> allRecipes = getAllRecipes();
        System.out.println("📊 Total recipes in database: " + allRecipes.size());
        
        if (allRecipes.isEmpty()) {
            System.out.println("❌ No recipes found in database");
            result.put("status", "info");
            result.put("message", "No recipes available");
            result.put("recipes", Collections.emptyList());
            result.put("userInventorySize", userInventory.size());
            result.put("totalRecipesFound", 0);
            return result;
        }

        // Debug: Show first few recipes and their ingredients
        System.out.println("📝 Sample recipes from database:");
        for (int i = 0; i < Math.min(3, allRecipes.size()); i++) {
            Recipe recipe = allRecipes.get(i);
            System.out.println("  Recipe " + i + ": " + recipe.getRecipeName());
            System.out.println("  Ingredients: " + (recipe.getIngredients() != null ? 
                recipe.getIngredients().substring(0, Math.min(100, recipe.getIngredients().length())) : "NULL"));
        }

        // Find matching recipes using simple text matching
        List<Map<String, Object>> matchingRecipes = new ArrayList<>();
        
        for (Recipe recipe : allRecipes) {
            if (recipe.getIngredients() == null) {
                continue;
            }
            
            String recipeIngredients = recipe.getIngredients().toLowerCase();
            int matchCount = 0;
            List<String> matchedItems = new ArrayList<>();
            List<String> missingItems = new ArrayList<>();
            
            // Check each user ingredient against recipe ingredients
            for (String userIngredient : userInventory) {
                String cleanUserIngredient = userIngredient.toLowerCase().trim();
                if (recipeIngredients.contains(cleanUserIngredient)) {
                    matchCount++;
                    matchedItems.add(userIngredient);
                    System.out.println("✅ MATCH: Recipe '" + recipe.getRecipeName() + 
                                     "' contains '" + userIngredient + "'");
                }
            }
            
            if (matchCount > 0) {
                // Calculate match percentage
                List<String> allRecipeIngredients = recipe.getIngredientList();
                double matchPercentage = allRecipeIngredients.isEmpty() ? 0 : 
                    (double) matchCount / allRecipeIngredients.size() * 100;
                
                // Find missing ingredients
                for (String recipeIng : allRecipeIngredients) {
                    boolean found = false;
                    for (String userIng : userInventory) {
                        if (recipeIng.toLowerCase().contains(userIng.toLowerCase())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        missingItems.add(recipeIng);
                    }
                }
                
                Map<String, Object> recipeResult = new HashMap<>();
                recipeResult.put("recipe", recipe);
                recipeResult.put("matchingIngredients", matchCount);
                recipeResult.put("totalIngredients", allRecipeIngredients.size());
                recipeResult.put("matchPercentage", Math.round(matchPercentage));
                recipeResult.put("matchedItems", matchedItems);
                recipeResult.put("missingIngredients", missingItems);
                recipeResult.put("missingCount", missingItems.size());
                
                matchingRecipes.add(recipeResult);
                System.out.println("🎯 ADDED: " + recipe.getRecipeName() + " - " + matchCount + " matches");
            }
        }

        // Sort by match percentage (highest first), then by missing ingredients (lowest first)
        matchingRecipes.sort((r1, r2) -> {
            double p1 = (double) r1.get("matchPercentage");
            double p2 = (double) r2.get("matchPercentage");
            int missing1 = (int) r1.get("missingCount");
            int missing2 = (int) r2.get("missingCount");
            
            if (p1 != p2) {
                return Double.compare(p2, p1);
            }
            return Integer.compare(missing1, missing2);
        });

        result.put("status", "success");
        result.put("totalRecipesFound", matchingRecipes.size());
        result.put("userInventorySize", userInventory.size());
        result.put("recipes", matchingRecipes);
        
        System.out.println("✅ FINAL: Found " + matchingRecipes.size() + " matching recipes");
        System.out.println("=== MEAL CHALLENGES DEBUG END ===");
        
        return result;
    }

    public List<Recipe> findRecipesByUserInventory(Long userId, List<String> userInventory) {
        if (userInventory == null || userInventory.isEmpty()) {
            return Collections.emptyList();
        }

        List<Recipe> allRecipes = getAllRecipes();
        return allRecipes.stream()
                .filter(recipe -> hasMatchingIngredients(recipe, userInventory))
                .sorted((r1, r2) -> {
                    int matches1 = r1.countMatchingIngredients(userInventory);
                    int matches2 = r2.countMatchingIngredients(userInventory);
                    return Integer.compare(matches2, matches1);
                })
                .collect(Collectors.toList());
    }

    private boolean hasMatchingIngredients(Recipe recipe, List<String> userIngredients) {
        if (recipe.getIngredients() == null) return false;
        
        String recipeIngredients = recipe.getIngredients().toLowerCase();
        for (String userIng : userIngredients) {
            if (recipeIngredients.contains(userIng.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public List<Recipe> searchRecipes(String query) {
        return recipeRepository.findByRecipeNameContainingIgnoreCase(query);
    }

    public List<Recipe> getRecipesByCategory(String category) {
        return recipeRepository.findByCuisinePathContainingIgnoreCase(category);
    }

    public Recipe saveRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public Optional<Recipe> getRecipeById(Long id) {
        return recipeRepository.findById(id);
    }

    public void deleteRecipe(Long id) {
        recipeRepository.deleteById(id);
    }
}