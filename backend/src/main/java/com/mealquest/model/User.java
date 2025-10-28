package com.mealquest.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Email
    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;

    // FIX: Match the actual column name in database
    @Column(name = "inventory_json", columnDefinition = "TEXT")
    private String inventoryJson = "[]";

    // Constructors
    public User() {}

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.inventoryJson = "[]";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getInventoryJson() { return inventoryJson; }
    public void setInventoryJson(String inventoryJson) { this.inventoryJson = inventoryJson; }

    // Helper methods to work with inventory as List
    public List<String> getInventory() {
        try {
            if (inventoryJson == null || inventoryJson.trim().isEmpty()) {
                return new ArrayList<>();
            }
            // Simple JSON parsing - in production use a proper JSON library
            String cleanJson = inventoryJson.replace("[", "").replace("]", "").replace("\"", "");
            if (cleanJson.trim().isEmpty()) {
                return new ArrayList<>();
            }
            List<String> inventory = new ArrayList<>();
            for (String item : cleanJson.split(",")) {
                if (!item.trim().isEmpty()) {
                    inventory.add(item.trim());
                }
            }
            return inventory;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void setInventory(List<String> inventory) {
        if (inventory == null || inventory.isEmpty()) {
            this.inventoryJson = "[]";
        } else {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < inventory.size(); i++) {
                sb.append("\"").append(inventory.get(i)).append("\"");
                if (i < inventory.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append("]");
            this.inventoryJson = sb.toString();
        }
    }

    public void addToInventory(String ingredient) {
        List<String> current = getInventory();
        if (!current.contains(ingredient)) {
            current.add(ingredient);
            setInventory(current);
        }
    }

    public void removeFromInventory(String ingredient) {
        List<String> current = getInventory();
        current.remove(ingredient);
        setInventory(current);
    }
}