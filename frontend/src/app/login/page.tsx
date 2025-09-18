"use client";

import React, { useState } from "react";
import { useRouter } from "next/navigation";

interface LoginRequest {
  username: string;
  password: string;
}

interface AuthResponse {
  status: string;
  message: string;
  userId?: number;
}

export default function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const router = useRouter();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      const loginRequest: LoginRequest = { username, password };
      
      // Replace this URL with your deployed backend later
      const res = await fetch(`/api/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(loginRequest),
      });

      if (!res.ok) {
        throw new Error("Login failed");
      }

      const data: AuthResponse = await res.json();
      
      if (data.status === "SUCCESS" && data.userId) {
        localStorage.setItem("mealquest_userId", data.userId.toString());
        router.push("/home");
      } else {
        setError(data.message || "Invalid credentials");
      }
    } catch (err) {
      setError("Login failed. Please check your credentials.");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center p-4 bg-gray-900">
      <div className="bg-gray-800 p-8 rounded-lg shadow-xl w-96">
        <h1 className="text-3xl font-bold mb-6 text-center text-white">MealQuest Login</h1>
        
        {error && (
          <div className="bg-red-500 text-white p-3 rounded mb-4">
            {error}
          </div>
        )}
        
        <form onSubmit={handleLogin} className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-2 text-gray-300">
              Username
            </label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Enter your username"
              className="w-full p-3 border border-gray-600 rounded bg-gray-700 text-white"
              required
              disabled={loading}
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium mb-2 text-gray-300">
              Password
            </label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Enter your password"
              className="w-full p-3 border border-gray-600 rounded bg-gray-700 text-white"
              required
              disabled={loading}
            />
          </div>
          
          <button
            type="submit"
            disabled={loading}
            className="w-full bg-blue-600 hover:bg-blue-700 text-white p-3 rounded font-medium disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {loading ? (
              <div className="flex items-center justify-center">
                <div className="loading-spinner mr-2"></div>
                Logging in...
              </div>
            ) : (
              "Login"
            )}
          </button>
        </form>
        
        <div className="mt-6 text-center">
          <p className="text-gray-400">
            Don&apos;t have an account?{" "}
            <a
              href="/register"
              className="text-blue-400 hover:text-blue-300 underline"
            >
              Register here
            </a>
          </p>
        </div>
      </div>
    </div>
  );
}
