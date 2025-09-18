import { NextConfig } from 'next'

const nextConfig: NextConfig = {
  output: 'export',                  // enable static HTML export
  basePath: '/MealFinder',           // your repo name
  assetPrefix: '/MealFinder/',       // ensures _next assets load
  images: { unoptimized: true },     // disable image optimization
}

export default nextConfig
