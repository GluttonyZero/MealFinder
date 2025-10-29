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
        // Drop the old relational tables
        jdbcTemplate.execute("DROP TABLE IF EXISTS recipe_ingredients");
        jdbcTemplate.execute("DROP TABLE IF EXISTS recipes");
        jdbcTemplate.execute("DROP TABLE IF EXISTS ingredients");
        
        // Keep users table
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "username VARCHAR(255) NOT NULL UNIQUE, " +
            "password VARCHAR(255) NOT NULL, " +
            "email VARCHAR(255) NOT NULL UNIQUE, " +
            "inventory_json TEXT" +
            ")");
        
        // Create the flat recipes table (your preferred schema)
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS recipes (" +
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
            ")");
        
        response.put("status", "success");
        response.put("message", "Database tables created with flat structure");
        
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