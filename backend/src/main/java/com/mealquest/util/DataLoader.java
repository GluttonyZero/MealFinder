package com.mealquest.util;

import com.mealquest.model.User;
import com.mealquest.model.Ingredient;
import com.mealquest.model.Recipe;
import com.mealquest.repository.UserRepository;
import com.mealquest.repository.IngredientRepository;
import com.mealquest.repository.RecipeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;
@Component
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    public DataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder, JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        System.out.println("🚀 Starting database initialization...");
        
        // First, ensure tables exist
        ensureTablesExist();
        
        // Then create sample data
        if (userRepository.count() == 0) {
            createSampleUsers();
        } else {
            System.out.println("✅ Database already has " + userRepository.count() + " users");
        }
    }

    private void ensureTablesExist() {
        try {
            // Try to query users table to see if it exists
            userRepository.count();
            System.out.println("✅ Database tables are ready");
        } catch (Exception e) {
            System.out.println("❌ Tables don't exist, creating them...");
            createTablesManually();
        }
    }

    private void createTablesManually() {
        try {
            jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS users (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(255) NOT NULL UNIQUE, " +
                "password VARCHAR(255) NOT NULL, " +
                "email VARCHAR(255) NOT NULL UNIQUE" +
                ")"
            );
            
            jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS user_inventory (" +
                "user_id BIGINT NOT NULL, " +
                "ingredient VARCHAR(255), " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                ")"
            );
            
            System.out.println("✅ Tables created manually");
        } catch (Exception e) {
            System.err.println("❌ Failed to create tables: " + e.getMessage());
        }
    }

    private void createSampleUsers() {
        try {
            User user1 = new User();
            user1.setUsername("alice");
            user1.setPassword(passwordEncoder.encode("password123"));
            user1.setEmail("alice@example.com");
            user1.setInventory(java.util.Arrays.asList("Chicken", "Rice", "Tomato"));

            User user2 = new User();
            user2.setUsername("bob");
            user2.setPassword(passwordEncoder.encode("password123"));
            user2.setEmail("bob@example.com");
            user2.setInventory(java.util.Arrays.asList("Pasta", "Cheese", "Garlic"));

            userRepository.save(user1);
            userRepository.save(user2);
            
            System.out.println("✅ Created 2 sample users with inventories");
            
        } catch (Exception e) {
            System.err.println("❌ Error creating sample users: " + e.getMessage());
        }
    }
}