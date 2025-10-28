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

    public List<Recipe> findRecipesByUserInventory(Long userId, List<String> userInventory) {
        if (userInventory == null || userInventory.isEmpty()) {
            return Collections.emptyList();
        }

        List<Recipe> matchingRecipes = recipeRepository.findRecipesByIngredientNames(userInventory);
        
        matchingRecipes.sort((r1, r2) -> {
            int matches1 = r1.countMatchingIngredients(userInventory);
            int matches2 = r2.countMatchingIngredients(userInventory);
            return Integer.compare(matches2, matches1);
        });
        
        return matchingRecipes;
    }

    public List<Recipe> findRecipesByAllIngredients(List<String> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            return Collections.emptyList();
        }
        
        Long ingredientCount = (long) ingredients.size();
        return recipeRepository.findRecipesByAllIngredients(ingredients, ingredientCount);
    }

    public List<Recipe> searchRecipes(String query) {
        return recipeRepository.findByNameContainingIgnoreCase(query);
    }

    public List<Recipe> getRecipesByCategory(String category) {
        return recipeRepository.findByCategory(category);
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

    public Map<String, Object> getRecipeSuggestionsWithScoring(Long userId, List<String> userInventory) {
        List<Recipe> matchingRecipes = findRecipesByUserInventory(userId, userInventory);
        
        List<Map<String, Object>> scoredRecipes = matchingRecipes.stream()
                .map(recipe -> {
                    int matchingCount = recipe.countMatchingIngredients(userInventory);
                    int totalIngredients = recipe.getIngredients().size();
                    double matchPercentage = (double) matchingCount / totalIngredients * 100;
                    
                    Map<String, Object> scoredRecipe = new HashMap<>();
                    scoredRecipe.put("recipe", recipe);
                    scoredRecipe.put("matchingIngredients", matchingCount);
                    scoredRecipe.put("totalIngredients", totalIngredients);
                    scoredRecipe.put("matchPercentage", Math.round(matchPercentage));
                    scoredRecipe.put("missingIngredients", totalIngredients - matchingCount);
                    
                    return scoredRecipe;
                })
                .collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("totalRecipesFound", scoredRecipes.size());
        result.put("userInventorySize", userInventory.size());
        result.put("recipes", scoredRecipes);
        
        return result;
    }
}