package com.mealquest.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_inventory")
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventory_id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "ingredient", nullable = false)
    private String ingredient;

    // Constructors
    public InventoryItem() {}

    public InventoryItem(User user, String ingredient) {
        this.user = user;
        this.ingredient = ingredient;
    }

    // Getters and Setters
    public Long getInventory_id() {
        return inventory_id;
    }

    public void setInventory_id(Long inventory_id) {
        this.inventory_id = inventory_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }
}