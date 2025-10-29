package com.mealquest.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recipes")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipe_name")
    private String name;
    
    private String prepTime;
    private String cookTime;
    private String totalTime;
    private String servings;
    private String yield;
    
    @Column(columnDefinition = "TEXT")
    private String ingredients;  // Store as text like your database
    
    @Column(columnDefinition = "TEXT")
    private String directions;
    
    private Float rating;
    private String url;
    private String cuisinePath;
    
    @Column(columnDefinition = "TEXT")
    private String nutrition;
    
    private String timing;
    private String imgSrc;

    // Constructors
    public Recipe() {}

    public Recipe(String name, String ingredients, String directions, String category) {
        this.name = name;
        this.ingredients = ingredients;
        this.directions = directions;
        this.cuisinePath = category;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPrepTime() { return prepTime; }
    public void setPrepTime(String prepTime) { this.prepTime = prepTime; }

    public String getCookTime() { return cookTime; }
    public void setCookTime(String cookTime) { this.cookTime = cookTime; }

    public String getTotalTime() { return totalTime; }
    public void setTotalTime(String totalTime) { this.totalTime = totalTime; }

    public String getServings() { return servings; }
    public void setServings(String servings) { this.servings = servings; }

    public String getYield() { return yield; }
    public void setYield(String yield) { this.yield = yield; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public String getDirections() { return directions; }
    public void setDirections(String directions) { this.directions = directions; }

    public Float getRating() { return rating; }
    public void setRating(Float rating) { this.rating = rating; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getCuisinePath() { return cuisinePath; }
    public void setCuisinePath(String cuisinePath) { this.cuisinePath = cuisinePath; }

    public String getNutrition() { return nutrition; }
    public void setNutrition(String nutrition) { this.nutrition = nutrition; }

    public String getTiming() { return timing; }
    public void setTiming(String timing) { this.timing = timing; }

    public String getImgSrc() { return imgSrc; }
    public void setImgSrc(String imgSrc) { this.imgSrc = imgSrc; }

    // Helper method to check if recipe contains an ingredient
    public boolean containsIngredient(String ingredientName) {
        if (ingredients == null || ingredientName == null) return false;
        
        String lowerIngredients = ingredients.toLowerCase();
        String lowerIngredient = ingredientName.toLowerCase().trim();
        
        // Split ingredients by commas and check each one
        String[] ingredientList = ingredients.split(",");
        for (String ing : ingredientList) {
            if (ing.toLowerCase().contains(lowerIngredient)) {
                return true;
            }
        }
        return false;
    }

    // Count how many user ingredients match this recipe
    public int countMatchingIngredients(List<String> userIngredients) {
        if (userIngredients == null || ingredients == null) return 0;
        
        int matches = 0;
        for (String userIng : userIngredients) {
            if (containsIngredient(userIng)) {
                matches++;
            }
        }
        return matches;
    }

    // Get list of individual ingredients
    public List<String> getIngredientList() {
        List<String> result = new ArrayList<>();
        if (ingredients != null) {
            String[] parts = ingredients.split(",");
            for (String part : parts) {
                String cleaned = part.trim();
                if (!cleaned.isEmpty()) {
                    result.add(cleaned);
                }
            }
        }
        return result;
    }
}