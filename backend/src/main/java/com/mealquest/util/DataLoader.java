package com.mealquest.util;

import com.mealquest.model.User;
import com.mealquest.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        System.out.println("🚀 Starting database initialization...");
        
        if (userRepository.count() == 0) {
            createSampleUsers();
            System.out.println("✅ Sample users added!");
        } else {
            System.out.println("✅ Database already has " + userRepository.count() + " users");
        }
    }

    private void createSampleUsers() {
        try {
            User u1 = new User();
            u1.setUsername("alice");
            u1.setPassword(passwordEncoder.encode("password123"));
            u1.setEmail("alice@example.com");
            u1.setInventory(Arrays.asList("Chicken", "Rice", "Tomato"));

            User u2 = new User();
            u2.setUsername("bob");
            u2.setPassword(passwordEncoder.encode("password123"));
            u2.setEmail("bob@example.com");
            u2.setInventory(Arrays.asList("Pasta", "Cheese", "Garlic"));

            User u3 = new User();
            u3.setUsername("charlie");
            u3.setPassword(passwordEncoder.encode("password123"));
            u3.setEmail("charlie@example.com");
            u3.setInventory(Arrays.asList("Eggs", "Milk", "Flour"));

            userRepository.save(u1);
            userRepository.save(u2);
            userRepository.save(u3);
            
            System.out.println("✅ Created 3 sample users with inventories");
            
        } catch (Exception e) {
            System.err.println("❌ Error creating sample users: " + e.getMessage());
            e.printStackTrace();
        }
    }
}