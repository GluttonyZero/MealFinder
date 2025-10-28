package com.mealquest.repository;

import com.mealquest.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    
    @Query("SELECT DISTINCT r FROM Recipe r JOIN r.ingredients i WHERE i.name IN :ingredientNames")
    List<Recipe> findRecipesByIngredientNames(@Param("ingredientNames") List<String> ingredientNames);
    
    @Query("SELECT r FROM Recipe r WHERE " +
           "(SELECT COUNT(DISTINCT i) FROM r.ingredients i WHERE i.name IN :ingredientNames) = :ingredientCount")
    List<Recipe> findRecipesByAllIngredients(@Param("ingredientNames") List<String> ingredientNames, 
                                           @Param("ingredientCount") Long ingredientCount);
    
    List<Recipe> findByCategory(String category);
    List<Recipe> findByNameContainingIgnoreCase(String name);
}