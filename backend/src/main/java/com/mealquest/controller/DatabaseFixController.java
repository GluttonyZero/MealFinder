package com.mealquest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class DatabaseFixController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/api/fix-database")
    public Map<String, Object> fixDatabase() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Drop tables if they exist (in correct order due to foreign keys)
            jdbcTemplate.execute("DROP TABLE IF EXISTS recipe_ingredients");
            jdbcTemplate.execute("DROP TABLE IF EXISTS recipes");
            jdbcTemplate.execute("DROP TABLE IF EXISTS ingredients");
            jdbcTemplate.execute("DROP TABLE IF EXISTS users");
            
            // Create users table
            jdbcTemplate.execute(
                "CREATE TABLE users (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(255) NOT NULL UNIQUE, " +
                "password VARCHAR(255) NOT NULL, " +
                "email VARCHAR(255) NOT NULL UNIQUE, " +
                "inventory_json TEXT" +
                ")"
            );
            
            // Create the recipes table that matches your existing schema
            jdbcTemplate.execute(
                "CREATE TABLE recipes (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "recipe_name VARCHAR(500), " +
                "prep_time VARCHAR(500), " +
                "cook_time VARCHAR(500), " +
                "total_time VARCHAR(500), " +
                "servings VARCHAR(500), " +
                "`yield` VARCHAR(500), " +
                "ingredients TEXT, " +
                "directions TEXT, " +
                "rating FLOAT, " +
                "url VARCHAR(500), " +
                "cuisine_path VARCHAR(500), " +
                "nutrition TEXT, " +
                "timing VARCHAR(500), " +
                "img_src VARCHAR(500)" +
                ")"
            );
            
            response.put("status", "success");
            response.put("message", "Database tables created successfully");
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        
        return response;
    }

    @GetMapping("/api/add-sample-recipes")
    public Map<String, Object> addSampleRecipes() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Add sample recipes that will match common ingredients
            jdbcTemplate.execute(
                "INSERT IGNORE INTO recipes (recipe_name, ingredients, directions, cuisine_path, rating, img_src) VALUES " +
                "('Apple Pie', 'apples, sugar, flour, butter, cinnamon, vanilla extract', 'Mix ingredients and bake at 350°F for 45 minutes', 'Desserts', 4.5, 'https://example.com/apple-pie.jpg'), " +
                "('Apple Crumble', 'apples, brown sugar, flour, butter, oats, cinnamon', 'Layer apples with crumble topping and bake until golden', 'Desserts', 4.3, 'https://example.com/apple-crumble.jpg'), " +
                "('Apple Salad', 'apples, mixed greens, walnuts, feta cheese, olive oil, lemon juice', 'Chop ingredients and toss with dressing', 'Salads', 4.0, 'https://example.com/apple-salad.jpg'), " +
                "('Scrambled Eggs', 'eggs, butter, salt, black pepper, milk, chives', 'Whisk eggs with milk and cook in butter until fluffy', 'Breakfast', 4.2, 'https://example.com/eggs.jpg'), " +
                "('Tomato Pasta', 'pasta, tomatoes, garlic, olive oil, basil, salt, pepper', 'Cook pasta and mix with fresh tomato sauce', 'Main Course', 4.4, 'https://example.com/pasta.jpg')"
            );
            
            // Check how many recipes were added
            Integer recipeCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM recipes", Integer.class);
            
            response.put("status", "success");
            response.put("message", "Added sample recipes");
            response.put("totalRecipes", recipeCount);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        
        return response;
    }

    @GetMapping("/api/check-database")
    public Map<String, Object> checkDatabase() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if tables exist and have data
            Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
            Integer recipeCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM recipes", Integer.class);
            
            response.put("users_table", userCount + " users");
            response.put("recipes_table", recipeCount + " recipes");
            response.put("database", "HEALTHY");
            
        } catch (Exception e) {
            response.put("users_table", "ERROR");
            response.put("recipes_table", "ERROR");
            response.put("database", "ERROR: " + e.getMessage());
        }
        
        return response;
    }
}