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
            // Drop tables if they exist
            jdbcTemplate.execute("DROP TABLE IF EXISTS users");
            
            // Create users table with proper structure
            jdbcTemplate.execute(
                "CREATE TABLE users (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(255) NOT NULL UNIQUE, " +
                "password VARCHAR(255) NOT NULL, " +
                "email VARCHAR(255) NOT NULL UNIQUE, " +
                "inventory_json TEXT DEFAULT '[]'" +
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