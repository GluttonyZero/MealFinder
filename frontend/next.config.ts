import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  output: "export",                  // static HTML export
  basePath: "/MealFinder",           // repo name
  assetPrefix: "/MealFinder/",       // ensures assets load correctly
  images: {
    unoptimized: true,               // disable image optimization
  },
};

export default nextConfig;
