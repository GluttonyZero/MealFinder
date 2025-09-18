"use client";
import React, { useEffect, useState } from "react";
import Inventory from "../components/Inventory";
import { User } from "../types/user";
const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;


export default function InventoryPage() {
  const [user, setUser] = useState<User | null>(null);

  useEffect(() => {
    fetch(`${API_BASE_URL}/api/users/1`)
      .then((res) => res.json())
      .then((data) => setUser(data));
  }, []);

  if (!user) return <div className="text-white">Loading...</div>;

  return (
    <div className="p-8 bg-black min-h-screen">
      <h1 className="text-3xl font-bold mb-4 text-white">Inventory Page</h1>
      <Inventory user={user} />
    </div>
  );
}
