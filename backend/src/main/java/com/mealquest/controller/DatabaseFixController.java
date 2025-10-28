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
            
            // Create users table with proper MySQL syntax
            jdbcTemplate.execute(
                "CREATE TABLE users (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(255) NOT NULL UNIQUE, " +
                "password VARCHAR(255) NOT NULL, " +
                "email VARCHAR(255) NOT NULL UNIQUE, " +
                "inventory_json TEXT" +  // Removed DEFAULT '[]' for TEXT column
                ")"
            );
            
            // Create ingredients table
            jdbcTemplate.execute(
                "CREATE TABLE ingredients (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255), " +
                "cost DOUBLE, " +
                "category VARCHAR(255)" +
                ")"
            );
            
            // Create recipes table
            jdbcTemplate.execute(
                "CREATE TABLE recipes (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255), " +
                "description TEXT, " +
                "instructions TEXT, " +
                "category VARCHAR(255), " +
                "prep_time INT, " +
                "cook_time INT, " +
                "difficulty VARCHAR(255)" +
                ")"
            );
            
            // Create recipe_ingredients join table
            jdbcTemplate.execute(
                "CREATE TABLE recipe_ingredients (" +
                "recipe_id BIGINT, " +
                "ingredient_id BIGINT, " +
                "PRIMARY KEY (recipe_id, ingredient_id), " +
                "FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE" +
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

    @GetMapping("/api/check-database")
    public Map<String, Object> checkDatabase() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if users table exists and has data
            Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
            response.put("users_table", "EXISTS");
            response.put("user_count", userCount);
            response.put("database", "HEALTHY");
        } catch (Exception e) {
            response.put("users_table", "MISSING");
            response.put("database", "ERROR: " + e.getMessage());
        }
        
        return response;
    }
}