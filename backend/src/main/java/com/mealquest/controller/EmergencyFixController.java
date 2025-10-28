// EmergencyFixController.java
package com.mealquest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class EmergencyFixController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/api/fix-tables")
    public Map<String, Object> fixTables() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Drop tables if they exist
            jdbcTemplate.execute("DROP TABLE IF EXISTS user_inventory");
            jdbcTemplate.execute("DROP TABLE IF EXISTS users");
            
            // Create users table
            jdbcTemplate.execute(
                "CREATE TABLE users (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(255) NOT NULL UNIQUE, " +
                "password VARCHAR(255) NOT NULL, " +
                "email VARCHAR(255) NOT NULL UNIQUE" +
                ")"
            );
            
            // Create inventory table
            jdbcTemplate.execute(
                "CREATE TABLE user_inventory (" +
                "user_id BIGINT NOT NULL, " +
                "ingredient VARCHAR(255), " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                ")"
            );
            
            response.put("status", "success");
            response.put("message", "Tables created successfully");
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        
        return response;
    }

    @GetMapping("/api/check-tables")
    public Map<String, Object> checkTables() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if users table exists
            jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users", 
                Integer.class
            );
            response.put("users_table", "EXISTS");
        } catch (Exception e) {
            response.put("users_table", "MISSING");
        }
        
        try {
            // Check if inventory table exists
            jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_inventory", 
                Integer.class
            );
            response.put("inventory_table", "EXISTS");
        } catch (Exception e) {
            response.put("inventory_table", "MISSING");
        }
        
        return response;
    }
}