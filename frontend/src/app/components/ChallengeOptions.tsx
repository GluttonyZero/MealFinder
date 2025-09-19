"use client";

import React, { useState } from "react";
import { User } from "../types/user";
import Image from "next/image";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

interface Props {
  user: User;
}

interface Meal {
  idMeal: string;
  strMeal: string;
  strMealThumb?: string;
  strInstructions?: string;
}

export default function ChallengeOptions({ user }: Props) {
  const [mealResult, setMealResult] = useState<Meal[] | null>(null);
  const [area, setArea] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const fetchMeals = async (url: string) => {
    setLoading(true);
    setError("");
    setMealResult(null);

    try {
      const res = await fetch(url);
      if (!res.ok) throw new Error("Failed to fetch meals");
      const data = await res.json();
      if (data.meals && Array.isArray(data.meals)) setMealResult(data.meals);
      else setError("No meals found. Try different ingredients!");
    } catch (err) {
      setError("Failed to fetch meals. Please try again.");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const option1 = async () => {
    if (!user.id) return setError("User not logged in");
    await fetchMeals(`${API_BASE_URL}/api/challenge/from-inventory/${user.id}`);
  };

  const option2 = async () => {
    await fetchMeals(`${API_BASE_URL}/api/challenge/random`);
  };

  const option3 = async () => {
    if (!area.trim()) return setError("Please enter a cuisine type");
    await fetchMeals(`${API_BASE_URL}/api/challenge/by-area?area=${encodeURIComponent(area)}`);
  };

  const option4 = async () => {
    await fetchMeals(`${API_BASE_URL}/api/challenge/budget`);
  };

  const viewRecipeDetails = async (mealId: string) => {
    try {
      const res = await fetch(`${API_BASE_URL}/api/challenge/lookup/${mealId}`);
      const data = await res.json();
      if (data.meals && data.meals[0]) {
        const meal = data.meals[0];
        alert(`Recipe: ${meal.strMeal}\n\nInstructions: ${meal.strInstructions?.substring(0, 200)}...`);
      }
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div>
      <h2 className="text-2xl font-bold mb-4">Meal Challenges</h2>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
        <button onClick={option1} disabled={loading} className="bg-green-600 hover:bg-green-700 text-white p-4 rounded-lg disabled:opacity-50">
          🏠 From My Inventory
        </button>

        <button onClick={option2} disabled={loading} className="bg-yellow-600 hover:bg-yellow-700 text-white p-4 rounded-lg disabled:opacity-50">
          🎲 Surprise Me
        </button>

        <div className="md:col-span-2 flex gap-2">
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
            className="bg-purple-600 hover:bg-purple-700 text-white px-6 py-3 rounded disabled:opacity-50"
          >
            ⏱️ Time Crunch
          </button>
        </div>

        <button onClick={option4} disabled={loading} className="md:col-span-2 bg-pink-600 hover:bg-pink-700 text-white p-4 rounded-lg disabled:opacity-50">
          💰 Budget Meals
        </button>
      </div>

      {error && <div className="bg-red-500 text-white p-3 rounded mb-4">{error}</div>}

      {mealResult && mealResult.length > 0 && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {mealResult.map((meal) => (
            <div key={meal.idMeal} className="bg-gray-700 p-4 rounded-lg">
              {meal.strMealThumb && (
                <Image
                  src={meal.strMealThumb}
                  alt={meal.strMeal}
                  width={400}
                  height={300}
                  className="rounded mb-3 object-cover"
                />
              )}
              <h4 className="font-bold text-lg mb-2">{meal.strMeal}</h4>
              <button onClick={() => viewRecipeDetails(meal.idMeal)} className="bg-blue-600 hover:bg-blue-700 text-white px-3 py-1 rounded text-sm">
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

      {loading && (
        <div className="text-center py-8">
          <div className="loading-spinner mx-auto mb-4"></div>
          <p className="text-gray-400">Finding delicious recipes...</p>
        </div>
      )}
    </div>
  );
}
