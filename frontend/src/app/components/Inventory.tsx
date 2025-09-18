// components/Inventory.tsx
"use client";

import React, { useEffect, useState } from "react";
import { User } from "../types/user";
import InventorySearch from "./InventorySearch";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

interface Props {
  user: User;
  onInventoryChange?: () => void;
}

export default function Inventory({ user, onInventoryChange }: Props) {
  const [inventory, setInventory] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (user?.id) fetchInventory();
  }, [user]);

  const fetchInventory = async () => {
    setLoading(true);
    setError("");
    try {
      const res = await fetch(`${API_BASE_URL}/api/users/${user.id}/inventory`);
      if (!res.ok) throw new Error(`Failed to fetch inventory: ${res.status}`);
      const data = await res.json();
      setInventory(Array.isArray(data) ? data : []);
    } catch (err) {
      setError("Failed to load inventory.");
      console.error(err);
      setInventory([]);
    } finally {
      setLoading(false);
    }
  };

  const addIngredient = async (ingredient: string) => {
    setLoading(true);
    setError("");
    try {
      const res = await fetch(
        `${API_BASE_URL}/api/users/${user.id}/inventory/add`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ ingredient }), // wrap in object for backend
        }
      );
      if (!res.ok) throw new Error(`Failed to add ingredient: ${res.status}`);
      const updated = await res.json();
      setInventory(Array.isArray(updated) ? updated : []);
      onInventoryChange?.();
    } catch (err) {
      setError("Network error. Please try again.");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const removeIngredient = async (ingredient: string) => {
    setLoading(true);
    setError("");
    try {
      const res = await fetch(
        `${API_BASE_URL}/api/users/${user.id}/inventory/remove`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ ingredient }),
        }
      );
      if (!res.ok) throw new Error(`Failed to remove ingredient: ${res.status}`);
      const updated = await res.json();
      setInventory(Array.isArray(updated) ? updated : []);
      onInventoryChange?.();
    } catch (err) {
      setError("Network error. Please try again.");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2 className="text-2xl font-bold mb-4">Your Inventory</h2>

      {error && (
        <div className="bg-red-500 text-white p-3 rounded mb-4">{error}</div>
      )}

      <InventorySearch onAddIngredient={addIngredient} disabled={loading} />

      <div className="mt-4">
        <h3 className="text-lg font-semibold mb-3">
          Current Ingredients ({inventory.length})
        </h3>

        {inventory.length === 0 ? (
          <div className="text-gray-400 p-4 text-center bg-gray-700 rounded">
            No ingredients in your inventory yet. Search above to add some!
          </div>
        ) : (
          <div className="grid grid-cols-2 md:grid-cols-3 gap-2">
            {inventory.map((item) => (
              <div
                key={item}
                className="bg-blue-600 text-white px-3 py-2 rounded flex items-center justify-between"
              >
                <span className="truncate">{item}</span>
                <button
                  onClick={() => removeIngredient(item)}
                  disabled={loading}
                  className="text-red-300 hover:text-white ml-2 disabled:opacity-50"
                  title="Remove ingredient"
                >
                  ✕
                </button>
              </div>
            ))}
          </div>
        )}
      </div>

      {loading && (
        <div className="mt-4 text-center">
          <div className="loading-spinner mx-auto"></div>
          <p className="text-gray-400 mt-2">Updating inventory...</p>
        </div>
      )}
    </div>
  );
}
