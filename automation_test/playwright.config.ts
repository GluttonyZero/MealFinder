import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  testDir: './tests',
  use: {
    headless: true,        // 👈 show the browser
    viewport: { width: 1280, height: 800 },
    ignoreHTTPSErrors: true,
  },
  projects: [
    {name: 'chromium', use: {...devices['Desktop Chrome'] } },
    {name: 'firefox', use: {...devices['Desktop Firefox'] } },
    { name: 'webkit', use: { ...devices['Desktop Safari'] } },
     {name: 'Mobile Chrome', use: {...devices['Pixel 5'] } },
      {name: 'Mobile Safari', use: {...devices['iphone 12'] } },
  ],
});
