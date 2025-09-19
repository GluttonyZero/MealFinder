package com.mealquest.controller;

import com.mealquest.model.User;
import com.mealquest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/challenge")
@CrossOrigin(origins = "https://gluttonyzero.github.io/MealFinder")
public class ChallengeController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    private final String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";

    // 1) Based on user inventory - REVISED FOR FREE API
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

    // Clean and remove duplicates
    List<String> cleanInventory = inventory.stream()
        .distinct()
        .map(String::trim)
        .collect(Collectors.toList());

    // Search for recipes using each ingredient individually
    Map<String, Map<String, ?>> allMeals = new LinkedHashMap<>();
    List<String> successfulIngredients = new ArrayList<>();

    for (String ingredient : cleanInventory) {
        try {
            Map<String, Object> result = searchByIngredient(ingredient);

            if (result != null && result.containsKey("meals") && result.get("meals") != null) {
                List<?> mealsRaw = (List<?>) result.get("meals");

                // Convert to Map<String, ?>
                List<Map<String, ?>> meals = mealsRaw.stream()
                    .filter(o -> o instanceof Map)
                    .map(o -> new HashMap<>((Map<String, ?>) o))
                    .collect(Collectors.toList());

                if (!meals.isEmpty()) {
                    successfulIngredients.add(ingredient);

                    // Add all meals to combined results without duplicates
                    for (Map<String, ?> meal : meals) {
                        String mealId = (String) meal.get("idMeal");
                        if (!allMeals.containsKey(mealId)) {
                            allMeals.put(mealId, meal);
                        }
                    }
                }
            }

            // Small delay to avoid rate limiting
            Thread.sleep(200);
        } catch (Exception e) {
            // Ignore failed ingredients
            continue;
        }
    }

    if (allMeals.isEmpty()) {
        return Map.of("status", "info", "message", "No recipes found for any ingredients in your inventory");
    }

    // Convert map values to list
    List<Map<String, ?>> combinedMeals = new ArrayList<>(allMeals.values());

    return Map.of(
        "status", "success",
        "successfulIngredients", successfulIngredients,
        "totalIngredientsTested", cleanInventory.size(),
        "meals", combinedMeals,
        "totalMealsFound", combinedMeals.size(),
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
