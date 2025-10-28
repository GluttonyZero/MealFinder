package com.mealquest.controller;

import com.mealquest.model.User;
import com.mealquest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:3000", "https://gluttonyzero.github.io", "https://mealfinder-0tmr.onrender.com"})
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // GET all users
    @GetMapping
    public List<User> getAllUsers() {
        System.out.println("Fetching all users...");
        List<User> users = userRepository.findAll();
        System.out.println("Found " + users.size() + " users");
        return users;
    }

    // GET user by id
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        System.out.println("Fetching user with id: " + id);
        return userRepository.findById(id)
                .map(user -> {
                    System.out.println("Found user: " + user.getUsername());
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // CREATE new user
    @PostMapping
    public User createUser(@RequestBody User user) {
        System.out.println("Creating user: " + user.getUsername());
        User savedUser = userRepository.save(user);
        System.out.println("User created with ID: " + savedUser.getId());
        return savedUser;
    }

    // DELETE user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        System.out.println("Deleting user with id: " + id);
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            System.out.println("User deleted successfully");
            return ResponseEntity.ok().build();
        }
        System.out.println("User not found for deletion");
        return ResponseEntity.notFound().build();
    }
}