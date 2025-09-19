package com.mealquest.controller;

import com.mealquest.model.User;
import com.mealquest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/challenge")
@CrossOrigin(origins = "https://gluttonyzero.github.io/MealFinder")
public class ChallengeController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    private final String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";

    // Option 3: From Inventory — fetch all meals once and filter locally
    @GetMapping("/from-inventory/{userId}")
    public Object fromInventory(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Map.of("status", "error", "message", "User not found");
        }

        List<String> inventory = user.getInventory();
        if (inventory == null || inventory.isEmpty()) {
            return Map.of("status", "info", "message", "Inventory empty");
        }

        // Clean and normalize inventory
        List<String> cleanInventory = inventory.stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .distinct()
                .toList();

        List<Map<String, Object>> matchingMeals = new ArrayList<>();

        try {
            // 1. Fetch all meals from MealDB
            String categoriesUrl = BASE_URL + "categories.php";
            Map<String, Object> categoriesResult = restTemplate.getForObject(categoriesUrl, Map.class);
            if (categoriesResult == null || !categoriesResult.containsKey("categories")) {
                return Map.of("status", "error", "message", "Could not fetch categories");
            }

            List<Map<String, Object>> categories = (List<Map<String, Object>>) categoriesResult.get("categories");

            // 2. Loop through each category and fetch meals
            for (Map<String, Object> category : categories) {
                String categoryName = (String) category.get("strCategory");
                String url = BASE_URL + "filter.php?c=" + URLEncoder.encode(categoryName, StandardCharsets.UTF_8);
                Map<String, Object> mealsResult = restTemplate.getForObject(url, Map.class);
                if (mealsResult == null || mealsResult.get("meals") == null) continue;

                List<Map<String, Object>> meals = (List<Map<String, Object>>) mealsResult.get("meals");
                for (Map<String, Object> mealSummary : meals) {
                    String mealId = (String) mealSummary.get("idMeal");
                    // Fetch full meal details
                    Map<String, Object> fullMealResult = restTemplate.getForObject(BASE_URL + "lookup.php?i=" + mealId, Map.class);
                    if (fullMealResult == null || fullMealResult.get("meals") == null) continue;

                    List<Map<String, Object>> fullMeals = (List<Map<String, Object>>) fullMealResult.get("meals");
                    if (fullMeals.isEmpty()) continue;
                    Map<String, Object> mealDetails = fullMeals.get(0);

                    // Collect all ingredients from meal
                    Set<String> mealIngredients = new HashSet<>();
                    for (int i = 1; i <= 20; i++) {
                        Object ing = mealDetails.get("strIngredient" + i);
                        if (ing != null) {
                            String ingStr = ing.toString().trim().toLowerCase();
                            if (!ingStr.isEmpty()) mealIngredients.add(ingStr);
                        }
                    }

                    // Check if any inventory ingredient is in meal
                    boolean matches = cleanInventory.stream().anyMatch(mealIngredients::contains);
                    if (matches) {
                        matchingMeals.add(mealDetails);
                    }
                }
            }

        } catch (Exception e) {
            return Map.of("status", "error", "message", "Error fetching meals: " + e.getMessage());
        }

        if (matchingMeals.isEmpty()) {
            return Map.of("status", "info", "message", "No recipes found for any ingredients in your inventory");
        }

        return Map.of(
                "status", "success",
                "inventoryUsed", cleanInventory,
                "meals", matchingMeals,
                "totalMealsFound", matchingMeals.size(),
                "note", "Showing recipes that contain ANY of your ingredients"
        );
    }


private Map<String, Object> searchByIngredient(String ingredient) {
    try {
        String encodedIngredient = URLEncoder.encode(ingredient, StandardCharsets.UTF_8.toString());
        String url = BASE_URL + "filter.php?i=" + encodedIngredient;
        return restTemplate.getForObject(url, Map.class);
    } catch (Exception e) {
        return null;
    }
}




    // 2) surprise me: random
    @GetMapping("/random")
    public Object random() {
        try {
            String url = BASE_URL + "random.php";
            Map<String, Object> result = restTemplate.getForObject(url, Map.class);

            if (result != null && result.containsKey("meals") && result.get("meals") != null) {
                return Map.of("status", "success", "meals", result.get("meals"));
            }
            return Map.of("status", "error", "message", "No random meal found");
        } catch (Exception e) {
            return Map.of("status", "error", "message", "Error fetching random meal: " + e.getMessage());
        }
    }

    // 3) time crunch by nationality/area
    @GetMapping("/by-area")
    public Object byArea(@RequestParam String area) {
        try {
            String encodedArea = URLEncoder.encode(area, StandardCharsets.UTF_8.toString());
            String url = BASE_URL + "filter.php?a=" + encodedArea;
            Map<String, Object> result = restTemplate.getForObject(url, Map.class);

            if (result != null && result.containsKey("meals") && result.get("meals") != null) {
                List<?> meals = (List<?>) result.get("meals");
                return Map.of("status", "success", "meals", meals);
            }
            return Map.of("status", "info", "message", "No recipes found for area: " + area);
        } catch (Exception e) {
            return Map.of("status", "error", "message", "Error fetching recipes by area: " + e.getMessage());
        }
    }

    // 4) budget: pick a few low-cost ingredients
    @GetMapping("/budget")
    public Object budget(@RequestParam(required = false) List<String> lowCost) {
        try {
            List<String> defaults = List.of("Eggs", "Rice", "Flour", "Potato", "Onion", "Tomato", "Pasta", "Beans");
            List<String> chosen = (lowCost == null || lowCost.isEmpty()) ? defaults : lowCost;

            Random random = new Random();
            int numberOfIngredients = Math.min(3, Math.max(1, random.nextInt(chosen.size()) + 1));

            // Pick one random ingredient (since multi-ingredient is premium)
            String ingredient = chosen.get(random.nextInt(chosen.size()));
            String encodedIngredient = URLEncoder.encode(ingredient, StandardCharsets.UTF_8.toString());

            String url = BASE_URL + "filter.php?i=" + encodedIngredient;
            Map<String, Object> result = restTemplate.getForObject(url, Map.class);

            if (result != null && result.containsKey("meals") && result.get("meals") != null) {
                List<?> meals = (List<?>) result.get("meals");
                return Map.of(
                    "status", "success",
                    "ingredientUsed", ingredient,
                    "meals", meals
                );
            }
            return Map.of("status", "info", "message", "No budget recipes found");
        } catch (Exception e) {
            return Map.of("status", "error", "message", "Error fetching budget recipes: " + e.getMessage());
        }
    }

    // 5) lookup details by id
    @GetMapping("/lookup/{id}")
    public Object lookup(@PathVariable String id) {
        try {
            String url = BASE_URL + "lookup.php?i=" + id;
            Map<String, Object> result = restTemplate.getForObject(url, Map.class);

            if (result != null && result.containsKey("meals") && result.get("meals") != null) {
                return Map.of("status", "success", "meals", result.get("meals"));
            }
            return Map.of("status", "error", "message", "Meal not found with ID: " + id);
        } catch (Exception e) {
            return Map.of("status", "error", "message", "Error looking up meal: " + e.getMessage());
        }
    }

    // 6) Get all categories
    @GetMapping("/categories")
    public Object getCategories() {
        try {
            String url = BASE_URL + "categories.php";
            Map<String, Object> result = restTemplate.getForObject(url, Map.class);
            return Map.of("status", "success", "data", result);
        } catch (Exception e) {
            return Map.of("status", "error", "message", "Error fetching categories: " + e.getMessage());
        }
    }

    // 7) Get all areas
    @GetMapping("/areas")
    public Object getAreas() {
        try {
            String url = BASE_URL + "list.php?a=list";
            Map<String, Object> result = restTemplate.getForObject(url, Map.class);
            return Map.of("status", "success", "data", result);
        } catch (Exception e) {
            return Map.of("status", "error", "message", "Error fetching areas: " + e.getMessage());
        }
    }

    // 8) Search by single ingredient
    @GetMapping("/by-ingredient")
    public Object byIngredient(@RequestParam String ingredient) {
        try {
            String encodedIngredient = URLEncoder.encode(ingredient, StandardCharsets.UTF_8.toString());
            String url = BASE_URL + "filter.php?i=" + encodedIngredient;
            Map<String, Object> result = restTemplate.getForObject(url, Map.class);

            if (result != null && result.containsKey("meals") && result.get("meals") != null) {
                List<?> meals = (List<?>) result.get("meals");
                return Map.of(
                    "status", "success",
                    "ingredient", ingredient,
                    "meals", meals,
                    "count", meals.size()
                );
            }
            return Map.of("status", "info", "message", "No recipes found for ingredient: " + ingredient);
        } catch (Exception e) {
            return Map.of("status", "error", "message", "Error fetching recipes: " + e.getMessage());
        }
    }
}
