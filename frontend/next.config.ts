import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  output: "export",                     // Static export
  assetPrefix: "/MealFinder/",          // Ensures CSS/JS load from correct path
  images: { unoptimized: true },        // Fix for GitHub Pages
};

export default nextConfig;
