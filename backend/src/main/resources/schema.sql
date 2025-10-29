-- ========================================
-- USERS TABLE
-- ========================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    inventory_json TEXT
);

-- ========================================
-- INGREDIENTS TABLE
-- ========================================
CREATE TABLE IF NOT EXISTS ingredients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    cost DOUBLE,
    category VARCHAR(255)
);

-- ========================================
-- RECIPES TABLE
-- ========================================
CREATE TABLE IF NOT EXISTS recipes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipe_name VARCHAR(500),
    prep_time VARCHAR(500),
    cook_time VARCHAR(500),
    total_time VARCHAR(500),
    servings VARCHAR(500),
    `yield` VARCHAR(500),
    ingredients TEXT,
    directions TEXT,
    rating FLOAT,
    url VARCHAR(500),
    cuisine_path VARCHAR(500),
    nutrition TEXT,
    timing VARCHAR(500),
    img_src VARCHAR(500)
);

-- ========================================
-- RECIPE_INGREDIENTS TABLE
-- ========================================
CREATE TABLE IF NOT EXISTS recipe_ingredients (
    recipe_id BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    PRIMARY KEY (recipe_id, ingredient_id),
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE
);