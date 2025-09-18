import { NextConfig } from 'next'

const nextConfig: NextConfig = {
  output: 'export',                  // enable static HTML export
  basePath: '/MealFinder',           // your repo name
  images: { unoptimized: true },     // disables next/image optimization
}

export default nextConfig
