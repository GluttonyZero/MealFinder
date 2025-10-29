// components/ChallengeOptions.tsx
"use client";

import React, { useState } from "react";
import Image from "next/image";
import { User } from "../types/user";
const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

interface Props {
  user: User;
}

interface BaseRecipe {
  id: number;
  name?: string;
  recipeName?: string;
  ingredients?: string;
  directions?: string;
  imgSrc?: string;
  rating?: number;
  cuisinePath?: string;
}

interface ScoredRecipe {
  recipe: BaseRecipe;
  matchingIngredients: number;
  totalIngredients: number;
  matchPercentage: number;
  missingIngredients: string[];
  missingCount: number;
}

interface Recipe extends BaseRecipe {
  matchPercentage?: number;
  matchingIngredients?: number;
  totalIngredients?: number;
  missingIngredients?: string[];
}

interface RecipeResponse {
  recipes: ScoredRecipe[] | Recipe[];
  status: string;
  message?: string;
  totalRecipesFound: number;
  userInventorySize: number;
}

export default function ChallengeOptions({ user }: Props) {
  const [mealResult, setMealResult] = useState<Recipe[] | null>(null);
  const [area, setArea] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const fetchRecipes = async (url: string) => {
    setLoading(true);
    setError("");
    setMealResult(null);
    
    try {
      console.log("Fetching from:", url);
      const res = await fetch(url);
      
      if (!res.ok) {
        throw new Error(`Failed to fetch recipes: ${res.status}`);
      }
      
      const data: RecipeResponse = await res.json();
      console.log("API Response:", data);
      
      if (data.status === "success" && Array.isArray(data.recipes)) {
        // Handle both scored recipes and direct recipes
        const recipes: Recipe[] = data.recipes.map((item: ScoredRecipe | Recipe) => {
          if ('recipe' in item) {
            // It's a ScoredRecipe with nested recipe object
            const scoredItem = item as ScoredRecipe;
            return {
              ...scoredItem.recipe,
              matchPercentage: scoredItem.matchPercentage,
              matchingIngredients: scoredItem.matchingIngredients,
              totalIngredients: scoredItem.totalIngredients,
              missingIngredients: scoredItem.missingIngredients
            };
          } else {
            // It's already a Recipe object
            return item as Recipe;
          }
        });
        setMealResult(recipes);
      } else if (data.status === "info" && data.message) {
        setError(data.message);
      } else if (Array.isArray(data)) {
        // Handle direct array response
        setMealResult(data as Recipe[]);
      } else {
        setError("No recipes found. Try different ingredients!");
      }
    } catch (err) {
      setError("Failed to fetch recipes. Please try again.");
      console.error("Fetch error:", err);
    } finally {
      setLoading(false);
    }
  };

  const option1 = async () => {
    await fetchRecipes(`${API_BASE_URL}/api/recipes/from-inventory/${user.id}`);
  };

  const option2 = async () => {
    await fetchRecipes(`${API_BASE_URL}/api/recipes`);
  };

  const option3 = async () => {
    if (!area.trim()) {
      setError("Please enter a cuisine type (e.g., Italian, Mexican)");
      return;
    }
    await fetchRecipes(`${API_BASE_URL}/api/recipes/category/${encodeURIComponent(area)}`);
  };

  const viewRecipeDetails = async (recipeId: number) => {
    try {
      const res = await fetch(`${API_BASE_URL}/api/recipes/${recipeId}`);
      const recipe: Recipe = await res.json();
      
      let details = `Recipe: ${recipe.name || recipe.recipeName}\n\n`;
      details += `Ingredients:\n${recipe.ingredients}\n\n`;
      details += `Instructions:\n${recipe.directions?.substring(0, 400)}...`;
      
      alert(details);
    } catch (err) {
      console.error("Failed to fetch recipe details:", err);
      alert("Failed to load recipe details");
    }
  };

  const getDisplayName = (recipe: Recipe): string => {
    return recipe.name || recipe.recipeName || 'Unnamed Recipe';
  };

  const getDisplayImage = (recipe: Recipe): string => {
    return recipe.imgSrc || '/placeholder-recipe.jpg';
  };

  return (
    <div>
      <h2 className="text-2xl font-bold mb-4">Meal Challenges</h2>
      
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
        <button 
          onClick={option1} 
          disabled={loading}
          className="bg-green-600 hover:bg-green-700 text-white p-4 rounded-lg disabled:opacity-50 transition-colors"
        >
          🏠 From My Inventory
        </button>
        
        <button 
          onClick={option2} 
          disabled={loading}
          className="bg-yellow-600 hover:bg-yellow-700 text-white p-4 rounded-lg disabled:opacity-50 transition-colors"
        >
          📋 All Recipes
        </button>
        
        <div className="md:col-span-2">
          <div className="flex gap-2">
            <input
              value={area}
              onChange={e => setArea(e.target.value)}
              placeholder="Enter cuisine (e.g., Italian, Mexican)"
              className="flex-1 p-3 border border-gray-600 rounded bg-gray-700 text-white"
              disabled={loading}
            />
            <button
              onClick={option3}
              disabled={loading || !area.trim()}
              className="bg-purple-600 hover:bg-purple-700 text-white px-6 py-3 rounded disabled:opacity-50 transition-colors"
            >
              🌎 By Cuisine
            </button>
          </div>
        </div>
      </div>

      {error && (
        <div className="bg-red-500 text-white p-3 rounded mb-4">
          {error}
        </div>
      )}

      {loading && (
        <div className="text-center py-8">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-white mx-auto mb-4"></div>
          <p className="text-gray-400">Finding delicious recipes...</p>
        </div>
      )}

      {mealResult && !loading && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {mealResult.map((recipe, index) => (
            <div key={recipe.id || index} className="bg-gray-700 p-4 rounded-lg">
              <div className="relative w-full h-48 mb-3 rounded overflow-hidden">
                <Image
                  src={getDisplayImage(recipe)}
                  alt={getDisplayName(recipe)}
                  fill
                  className="object-cover"
                  sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw"
                  onError={(e) => {
                    // Fallback to placeholder if image fails to load
                    const target = e.target as HTMLImageElement;
                    target.src = '/placeholder-recipe.jpg';
                  }}
                />
              </div>
              <h4 className="font-bold text-lg mb-2">
                {getDisplayName(recipe)}
              </h4>
              
              {recipe.matchPercentage !== undefined && (
                <div className="mb-2">
                  <div className="flex justify-between text-sm mb-1">
                    <span>Match: {recipe.matchPercentage}%</span>
                    <span>{recipe.matchingIngredients}/{recipe.totalIngredients} ingredients</span>
                  </div>
                  <div className="w-full bg-gray-600 rounded-full h-2">
                    <div 
                      className="bg-green-500 h-2 rounded-full transition-all duration-300"
                      style={{ width: `${recipe.matchPercentage}%` }}
                    ></div>
                  </div>
                </div>
              )}
              
              {recipe.rating && (
                <div className="text-yellow-400 text-sm mb-2">
                  ⭐ {recipe.rating}/5
                </div>
              )}
              
              {recipe.cuisinePath && (
                <div className="text-blue-400 text-sm mb-2">
                  🍽️ {recipe.cuisinePath}
                </div>
              )}
              
              <button
                onClick={() => viewRecipeDetails(recipe.id!)}
                className="bg-blue-600 hover:bg-blue-700 text-white px-3 py-1 rounded text-sm transition-colors"
              >
                View Recipe
              </button>
            </div>
          ))}
        </div>
      )}

      {!mealResult && !loading && !error && (
        <div className="text-gray-400 p-8 text-center bg-gray-700 rounded">
          Select a challenge above to discover new recipes!
        </div>
      )}
    </div>
  );
}