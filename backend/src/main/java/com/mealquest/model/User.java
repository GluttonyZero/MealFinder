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
    @Column(unique = true)
    private String username;

    @NotBlank
    private String password;

    @Email
    @NotBlank
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "user_inventory", 
        joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "ingredient")
    private List<String> inventory = new ArrayList<>();

    public User() {}

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<String> getInventory() { return inventory; }
    public void setInventory(List<String> inventory) { this.inventory = inventory; }

    public void addToInventory(String ingredient) {
        if (!inventory.contains(ingredient)) {
            inventory.add(ingredient);
        }
    }

    public void removeFromInventory(String ingredient) {
        inventory.remove(ingredient);
    }

    public void clearInventory() {
        inventory.clear();
    }

    public boolean hasIngredient(String ingredient) {
        return inventory.contains(ingredient);
    }

    public int getInventorySize() {
        return inventory.size();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", inventory=" + inventory +
                '}';
    }

    public static User createUser(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setInventory(new ArrayList<>());
        return user;
    }

    public void addMultipleToInventory(List<String> ingredients) {
        for (String ingredient : ingredients) {
            if (!inventory.contains(ingredient)) {
                inventory.add(ingredient);
            }
        }
    }

    public void removeMultipleFromInventory(List<String> ingredients) {
        inventory.removeAll(ingredients);
    }

    public boolean isInventoryEmpty() {
        return inventory.isEmpty();
    }

    public List<String> getInventoryCopy() {
        return new ArrayList<>(inventory);
    }
}