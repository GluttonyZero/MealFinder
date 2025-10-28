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
    private final IngredientRepository ingredientRepository;
    private final RecipeRepository recipeRepository;

    public DataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                     IngredientRepository ingredientRepository, RecipeRepository recipeRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.ingredientRepository = ingredientRepository;
        this.recipeRepository = recipeRepository;
    }

    @Override
    public void run(String... args) {
        loadUsers();
        loadIngredients();
        loadRecipes();
    }

    private void loadUsers() {
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

    private void loadIngredients() {
        if (ingredientRepository.count() == 0) {
            List<Ingredient> ingredients = List.of(
                new Ingredient("Chicken Breast", 5.99, "Meat"),
                new Ingredient("Rice", 2.49, "Grains"),
                new Ingredient("Tomato", 1.99, "Vegetables"),
                new Ingredient("Onion", 0.99, "Vegetables"),
                new Ingredient("Garlic", 0.79, "Vegetables"),
                new Ingredient("Olive Oil", 8.99, "Oils"),
                new Ingredient("Salt", 1.49, "Spices"),
                new Ingredient("Black Pepper", 2.99, "Spices"),
                new Ingredient("Pasta", 1.99, "Grains"),
                new Ingredient("Cheese", 4.99, "Dairy"),
                new Ingredient("Eggs", 3.49, "Dairy"),
                new Ingredient("Flour", 2.99, "Grains"),
                new Ingredient("Butter", 4.49, "Dairy"),
                new Ingredient("Milk", 3.99, "Dairy"),
                new Ingredient("Potato", 1.49, "Vegetables")
            );
            ingredientRepository.saveAll(ingredients);
            System.out.println("✅ Sample ingredients added!");
        }
    }

    private void loadRecipes() {
        if (recipeRepository.count() == 0) {
            List<Ingredient> allIngredients = ingredientRepository.findAll();
            
            // Recipe 1: Chicken and Rice
            Recipe chickenRice = new Recipe("Chicken and Rice", 
                "Simple and delicious chicken with rice", 
                "1. Cook chicken until golden brown\n2. Cook rice separately\n3. Combine and season with salt and pepper", 
                "Main Course");
            chickenRice.setPrepTime(10);
            chickenRice.setCookTime(25);
            chickenRice.setDifficulty("Easy");
            chickenRice.addIngredient(findIngredient(allIngredients, "Chicken Breast"));
            chickenRice.addIngredient(findIngredient(allIngredients, "Rice"));
            chickenRice.addIngredient(findIngredient(allIngredients, "Salt"));
            chickenRice.addIngredient(findIngredient(allIngredients, "Black Pepper"));
            chickenRice.addIngredient(findIngredient(allIngredients, "Olive Oil"));
            
            // Recipe 2: Tomato Pasta
            Recipe pastaDish = new Recipe("Tomato Pasta", 
                "Classic Italian pasta with tomato sauce", 
                "1. Cook pasta according to package instructions\n2. Sauté garlic and onion in olive oil\n3. Add tomatoes and simmer\n4. Combine with pasta", 
                "Main Course");
            pastaDish.setPrepTime(15);
            pastaDish.setCookTime(20);
            pastaDish.setDifficulty("Easy");
            pastaDish.addIngredient(findIngredient(allIngredients, "Pasta"));
            pastaDish.addIngredient(findIngredient(allIngredients, "Tomato"));
            pastaDish.addIngredient(findIngredient(allIngredients, "Garlic"));
            pastaDish.addIngredient(findIngredient(allIngredients, "Onion"));
            pastaDish.addIngredient(findIngredient(allIngredients, "Olive Oil"));
            pastaDish.addIngredient(findIngredient(allIngredients, "Salt"));
            
            // Recipe 3: Scrambled Eggs
            Recipe scrambledEggs = new Recipe("Scrambled Eggs", 
                "Fluffy and delicious scrambled eggs", 
                "1. Beat eggs with salt and pepper\n2. Melt butter in pan\n3. Cook eggs on low heat until fluffy", 
                "Breakfast");
            scrambledEggs.setPrepTime(5);
            scrambledEggs.setCookTime(10);
            scrambledEggs.setDifficulty("Very Easy");
            scrambledEggs.addIngredient(findIngredient(allIngredients, "Eggs"));
            scrambledEggs.addIngredient(findIngredient(allIngredients, "Butter"));
            scrambledEggs.addIngredient(findIngredient(allIngredients, "Salt"));
            scrambledEggs.addIngredient(findIngredient(allIngredients, "Black Pepper"));
            
            recipeRepository.save(chickenRice);
            recipeRepository.save(pastaDish);
            recipeRepository.save(scrambledEggs);
            
            System.out.println("✅ Sample recipes added!");
        }
    }

    private Ingredient findIngredient(List<Ingredient> ingredients, String name) {
        return ingredients.stream()
                .filter(ing -> ing.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}