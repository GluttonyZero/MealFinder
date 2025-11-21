package com.mealquest.repository;

import com.mealquest.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    List<Ingredient> findByNameContainingIgnoreCase(String name);
    List<Ingredient> findByCategory(String category);
    List<Ingredient> findByCostLessThanEqual(Double maxCost);
}