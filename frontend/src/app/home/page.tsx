// app/home/page.tsx
"use client";

import React, { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import Inventory from "../components/Inventory";
import ChallengeOptions from "../components/ChallengeOptions";
import { User } from "../types/user";
const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;


export default function HomePage() {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const router = useRouter();

  useEffect(() => {
    const userId = localStorage.getItem("mealquest_userId");
    if (!userId) {
      router.push("/login");
      return;
    }
    fetchUser(userId);
  }, [router]);

  const fetchUser = async (userId: string) => {
    try {
      const res = await fetch(`http://${API_BASE_URL}/api/users/${userId}`);
      if (res.ok) {
        const userData: User = await res.json();
        setUser(userData);
      } else {
        setError("Failed to load user data");
        router.push("/login");
      }
    } catch (error) {
      setError("Network error. Please try again.");
      console.error("Failed to fetch user:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("mealquest_userId");
    router.push("/login");
  };

  const refreshUser = async () => {
    if (!user) return;
    await fetchUser(user.id!.toString());
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-900">
        <div className="text-white text-xl">Loading...</div>
      </div>
    );
  }

  if (!user) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-900">
        <div className="text-red-500">Failed to load user data</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-900 text-white">
      <div className="container mx-auto px-4 py-8">
        {/* Header */}
        <div className="flex justify-between items-center mb-8 p-4 bg-gray-800 rounded-lg">
          <div>
            <h1 className="text-3xl font-bold">🍽️ MealQuest</h1>
            <p className="text-gray-400">Welcome back, {user.username}!</p>
          </div>
          <button
            onClick={handleLogout}
            className="bg-red-600 hover:bg-red-700 px-4 py-2 rounded transition-colors"
          >
            Logout
          </button>
        </div>

        {error && (
          <div className="bg-red-500 text-white p-3 rounded mb-4">
            {error}
          </div>
        )}

        {/* Main Content */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Inventory Section */}
          <div className="bg-gray-800 p-6 rounded-lg">
            <Inventory user={user} onInventoryChange={refreshUser} />
          </div>

          {/* Challenges Section */}
          <div className="bg-gray-800 p-6 rounded-lg">
            <ChallengeOptions user={user} />
          </div>
        </div>
      </div>
    </div>
  );
}