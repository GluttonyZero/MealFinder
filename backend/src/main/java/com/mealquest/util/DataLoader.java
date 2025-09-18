package com.mealquest.util;

import com.mealquest.model.User;
import com.mealquest.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
        if (userRepository.count() == 0) {
            User u1 = new User();
            u1.setUsername("alice");
            u1.setPassword(passwordEncoder.encode("password123"));
            u1.setEmail("alice@example.com");

            User u2 = new User();
            u2.setUsername("bob");
            u2.setPassword(passwordEncoder.encode("password123"));
            u2.setEmail("bob@example.com");

            User u3 = new User();
            u3.setUsername("charlie");
            u3.setPassword(passwordEncoder.encode("password123"));
            u3.setEmail("charlie@example.com");

            userRepository.save(u1);
            userRepository.save(u2);
            userRepository.save(u3);

            System.out.println("✅ Sample users added!");
        }
    }
}