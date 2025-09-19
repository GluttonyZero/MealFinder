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

    @GetMapping("/from-inventory/{userId}")
    public Object fromInventory(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return Map.of("status", "error", "message", "User not found");

        List<String> inventory = user.getInventory();
        if (inventory == null || inventory.isEmpty())
            return Map.of("status", "info", "message", "Inventory empty");

        List<Map<String, Object>> allMeals = new ArrayList<>();

        for (String ingredient : inventory) {
            try {
                String encoded = URLEncoder.encode(ingredient.trim(), StandardCharsets.UTF_8);
                Map<String, Object> result = restTemplate.getForObject(BASE_URL + "filter.php?i=" + encoded, Map.class);

                if (result != null && result.get("meals") != null) {
                    List<Map<String, Object>> meals = (List<Map<String, Object>>) result.get("meals");
                    allMeals.addAll(meals);
                }
            } catch (Exception e) {
                continue;
            }
        }

        if (allMeals.isEmpty())
            return Map.of("status", "info", "message", "No recipes found for any ingredients in your inventory");

        // Deduplicate by idMeal
        Map<String, Map<String, Object>> mealMap = new HashMap<>();
        for (Map<String, Object> meal : allMeals) {
            mealMap.put((String) meal.get("idMeal"), meal);
        }

        List<Map<String, Object>> uniqueMeals = new ArrayList<>(mealMap.values());

        // Fetch full details for each meal to get ingredients
        List<Map<String, Object>> detailedMeals = new ArrayList<>();
        for (Map<String, Object> meal : uniqueMeals) {
            try {
                String mealId = (String) meal.get("idMeal");
                Map<String, Object> details = restTemplate.getForObject(BASE_URL + "lookup.php?i=" + mealId, Map.class);
                if (details != null && details.get("meals") != null) {
                    Map<String, Object> fullMeal = ((List<Map<String, Object>>) details.get("meals")).get(0);

                    // Count how many ingredients match user inventory
                    int matchCount = 0;
                    for (int i = 1; i <= 20; i++) {
                        String key = "strIngredient" + i;
                        String ing = (String) fullMeal.get(key);
                        if (ing != null && !ing.isEmpty() && inventory.stream().anyMatch(inv -> inv.equalsIgnoreCase(ing.trim()))) {
                            matchCount++;
                        }
                    }
                    fullMeal.put("matchCount", matchCount);
                    detailedMeals.add(fullMeal);
                }
            } catch (Exception e) {
                continue;
            }
        }

        // Sort by number of ingredients matched (descending)
        detailedMeals.sort((a, b) -> ((Integer) b.get("matchCount")).compareTo((Integer) a.get("matchCount")));

        return Map.of(
                "status", "success",
                "totalIngredientsTested", inventory.size(),
                "totalMealsFound", detailedMeals.size(),
                "meals", detailedMeals,
                "note", "Recipes sorted by number of ingredients you already have (best matches first)"
        );
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
