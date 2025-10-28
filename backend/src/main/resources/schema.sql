-- Remove DEFAULT '[]' from TEXT columns
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    inventory_json TEXT
);

CREATE TABLE IF NOT EXISTS ingredients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    cost DOUBLE,
    category VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS recipes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    description TEXT,
    instructions TEXT,
    category VARCHAR(255),
    prep_time INT,
    cook_time INT,
    difficulty VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS recipe_ingredients (
    recipe_id BIGINT,
    ingredient_id BIGINT,
    PRIMARY KEY (recipe_id, ingredient_id),
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE
);