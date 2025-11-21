import { test, expect } from '@playwright/test';

test('Login works with real backend (cross-browser safe)', async ({ page }) => {
  // Go to login page
  await page.goto('https://gluttonyzero.github.io/MealFinder/login', { waitUntil: 'domcontentloaded' });

  // Focus and type username slowly to trigger React onChange
  const usernameInput = page.locator('input[placeholder="Enter your username"]');
  await usernameInput.focus();
  await usernameInput.type('bob', { delay: 100 });

  // Focus and type password slowly to trigger React onChange
  const passwordInput = page.locator('input[placeholder="Enter your password"]');
  await passwordInput.focus();
  await passwordInput.type('password123', { delay: 100 });

  // Wait 5 seconds to see what has been typed
  await page.waitForTimeout(5000);

  // Click login button
  await page.getByRole('button', { name: 'Login' }).click();

  // Wait for redirect to /home
  await page.waitForURL(/.*MealFinder\/home/);

  // Verify localStorage has userId
  const userId = await page.evaluate(() => localStorage.getItem('mealquest_userId'));
  expect(userId).not.toBeNull();

  // Verify home page heading
  await expect(page.locator('h1')).toHaveText(/MealQuest/i);

  // Optional: wait 2s to see home page
  await page.waitForTimeout(2000);
});
