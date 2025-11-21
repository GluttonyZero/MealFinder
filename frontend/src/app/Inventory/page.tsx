"use client";
import React, { useEffect, useState } from "react";
import Inventory from "../components/Inventory";
import { User } from "../types/user";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL; 

export default function InventoryPage() {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchUser = async () => {
      if (!API_BASE_URL) {
        setError("Backend URL not configured");
        setLoading(false);
        return;
      }

      try {
        const res = await fetch(`${API_BASE_URL}/api/users/1`);
        if (!res.ok) throw new Error("Failed to fetch user data");

        const data: User = await res.json();
        setUser(data);
      } catch (err) {
        console.error(err);
        setError("Failed to load user data. Please check backend URL.");
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, []);

  if (loading) return <div className="text-white">Loading...</div>;
  if (error) return <div className="text-red-500">{error}</div>;
  if (!user) return <div className="text-white">No user found</div>;

  return (
    <div className="p-8 bg-black min-h-screen">
      <h1 className="text-3xl font-bold mb-4 text-white">Inventory Page</h1>
      <Inventory user={user} />
    </div>
  );
}
