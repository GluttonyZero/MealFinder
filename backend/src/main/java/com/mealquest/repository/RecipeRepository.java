package com.mealquest.repository;

import com.mealquest.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    
    // Search by recipe name
    List<Recipe> findByRecipeNameContainingIgnoreCase(String name);
    
    // Search by cuisine path (category)
    List<Recipe> findByCuisinePathContainingIgnoreCase(String cuisinePath);
    
    // Custom query to search in ingredients text
    @Query("SELECT r FROM Recipe r WHERE LOWER(r.ingredients) LIKE LOWER(CONCAT('%', :ingredient, '%'))")
    List<Recipe> findByIngredientContaining(@Param("ingredient") String ingredient);
    
    // Search in multiple fields
    @Query("SELECT r FROM Recipe r WHERE " +
           "LOWER(r.recipeName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.ingredients) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.cuisinePath) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Recipe> searchInAllFields(@Param("query") String query);
}