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
        System.out.println("Searching recipes for user: " + userId + " with inventory: " + userInventory);
        
        if (userInventory == null || userInventory.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", "info");
            result.put("message", "Inventory is empty");
            result.put("recipes", Collections.emptyList());
            return result;
        }

        // Get ALL recipes first
        List<Recipe> allRecipes = getAllRecipes();
        System.out.println("Total recipes in database: " + allRecipes.size());
        
        // Filter recipes that match user ingredients
        List<Recipe> matchingRecipes = allRecipes.stream()
                .filter(recipe -> hasMatchingIngredients(recipe, userInventory))
                .collect(Collectors.toList());
        
        System.out.println("Found " + matchingRecipes.size() + " matching recipes");
        
        // Score and sort recipes by number of matching ingredients
        List<Map<String, Object>> scoredRecipes = matchingRecipes.stream()
                .map(recipe -> {
                    int matchingCount = recipe.countMatchingIngredients(userInventory);
                    List<String> allRecipeIngredients = recipe.getIngredientList();
                    double matchPercentage = (double) matchingCount / allRecipeIngredients.size() * 100;
                    
                    // Find which ingredients are missing
                    List<String> missingIngredients = getMissingIngredients(recipe, userInventory);
                    
                    Map<String, Object> scoredRecipe = new HashMap<>();
                    scoredRecipe.put("recipe", recipe);
                    scoredRecipe.put("matchingIngredients", matchingCount);
                    scoredRecipe.put("totalIngredients", allRecipeIngredients.size());
                    scoredRecipe.put("matchPercentage", Math.round(matchPercentage));
                    scoredRecipe.put("missingIngredients", missingIngredients);
                    scoredRecipe.put("missingCount", missingIngredients.size());
                    
                    return scoredRecipe;
                })
                .sorted((r1, r2) -> {
                    // Sort by match percentage (highest first), then by missing ingredients (lowest first)
                    double p1 = (double) r1.get("matchPercentage");
                    double p2 = (double) r2.get("matchPercentage");
                    int missing1 = (int) r1.get("missingCount");
                    int missing2 = (int) r2.get("missingCount");
                    
                    if (p1 != p2) {
                        return Double.compare(p2, p1);
                    }
                    return Integer.compare(missing1, missing2);
                })
                .collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("totalRecipesFound", scoredRecipes.size());
        result.put("userInventorySize", userInventory.size());
        result.put("recipes", scoredRecipes);
        
        return result;
    }

    private boolean hasMatchingIngredients(Recipe recipe, List<String> userIngredients) {
        for (String userIng : userIngredients) {
            if (recipe.containsIngredient(userIng)) {
                return true;
            }
        }
        return false;
    }

    private List<String> getMissingIngredients(Recipe recipe, List<String> userIngredients) {
        List<String> missing = new ArrayList<>();
        List<String> recipeIngredients = recipe.getIngredientList();
        
        for (String recipeIng : recipeIngredients) {
            boolean found = false;
            for (String userIng : userIngredients) {
                if (recipeIng.toLowerCase().contains(userIng.toLowerCase())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                missing.add(recipeIng);
            }
        }
        return missing;
    }

    // Update other methods to work with new schema
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

    public List<Recipe> searchRecipes(String query) {
        return recipeRepository.findByNameContainingIgnoreCase(query);
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