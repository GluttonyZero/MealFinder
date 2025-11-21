package com.mealquest.service;

import com.mealquest.model.Ingredient;
import com.mealquest.repository.IngredientRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class IngredientService {
    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public List<Ingredient> searchIngredients(String query) {
        return ingredientRepository.findByNameContainingIgnoreCase(query);
    }

    public Ingredient addIngredient(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    public void deleteIngredient(Long id) {
        ingredientRepository.deleteById(id);
    }

    public List<Ingredient> getIngredientsByCategory(String category) {
        return ingredientRepository.findByCategory(category);
    }

    public List<Ingredient> getBudgetIngredients(Double maxCost) {
        return ingredientRepository.findByCostLessThanEqual(maxCost);
    }
}