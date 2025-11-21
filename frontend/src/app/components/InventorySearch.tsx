// components/InventorySearch.tsx
"use client";

import React, { useState, useEffect } from "react";

interface InventorySearchProps {
  onAddIngredient: (ingredient: string) => void;
  disabled?: boolean;
}

interface MealDBIngredient {
  strIngredient: string;
}

interface MealDBResponse {
  meals: MealDBIngredient[];
}

const InventorySearch: React.FC<InventorySearchProps> = ({ onAddIngredient, disabled = false }) => {
  const [search, setSearch] = useState("");
  const [suggestions, setSuggestions] = useState<string[]>([]);
  const [allIngredients, setAllIngredients] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchIngredients = async () => {
      setLoading(true);
      try {
        const res = await fetch("https://www.themealdb.com/api/json/v1/1/list.php?i=list");
        const data: MealDBResponse = await res.json();
        if (data.meals) {
          const list: string[] = data.meals.map((m: MealDBIngredient) => m.strIngredient);
          setAllIngredients(list);
        }
      } catch (err) {
        console.error("Failed to load ingredients:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchIngredients();
  }, []);

  useEffect(() => {
    if (!search) {
      setSuggestions([]);
      return;
    }
    const filtered = allIngredients
      .filter((i) => i.toLowerCase().includes(search.toLowerCase()))
      .slice(0, 10);
    setSuggestions(filtered);
  }, [search, allIngredients]);

  const handleAdd = (ingredient: string) => {
    try {
      onAddIngredient(ingredient);
      setSearch("");
      setSuggestions([]);
    } catch (err) {
      console.error("Failed to add ingredient:", err);
    }
  };

  return (
    <div className="mb-4 w-full relative">
      <input
        type="text"
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        placeholder="Search for ingredients..."
        className="border border-gray-600 p-3 w-full rounded bg-gray-700 text-white placeholder-gray-400"
        disabled={disabled || loading}
      />

      {loading && (
        <div className="absolute right-3 top-3">
          <div className="loading-spinner"></div>
        </div>
      )}

      {suggestions.length > 0 && (
        <ul className="absolute z-10 border border-gray-600 bg-gray-800 max-h-40 overflow-y-auto w-full rounded mt-1 shadow-lg">
          {suggestions.map((item) => (
            <li
              key={item}
              className="cursor-pointer hover:bg-gray-700 p-3 text-white border-b border-gray-600 last:border-b-0"
              onClick={() => handleAdd(item)}
            >
              {item}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default InventorySearch;