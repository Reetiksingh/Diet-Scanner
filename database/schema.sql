-- Smart Diet Scanner Database Schema
-- Create Database
CREATE DATABASE IF NOT EXISTS diet_scanner;
USE diet_scanner;
-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    disease ENUM('diabetes', 'pcod', 'thyroid', 'obesity', 'bp') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
-- 2. Food Table
CREATE TABLE IF NOT EXISTS food (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    barcode VARCHAR(50) UNIQUE,
    description TEXT,
    category VARCHAR(100),
    brand VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- 3. Nutrition Table
CREATE TABLE IF NOT EXISTS nutrition (
    id INT PRIMARY KEY AUTO_INCREMENT,
    food_id INT NOT NULL,
    calories INT,
    sugar FLOAT,
    carbs FLOAT,
    protein FLOAT,
    fat FLOAT,
    saturated_fat FLOAT,
    fiber FLOAT,
    sodium FLOAT,
    potassium FLOAT,
    iron FLOAT,
    calcium FLOAT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (food_id) REFERENCES food(id)
);
-- 4. Diseases Table
CREATE TABLE IF NOT EXISTS diseases (
    id INT PRIMARY KEY AUTO_INCREMENT,
    disease_name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    symptoms TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- 5. Disease Rules Table
CREATE TABLE IF NOT EXISTS disease_rules (
    id INT PRIMARY KEY AUTO_INCREMENT,
    disease_id INT NOT NULL,
    nutrient VARCHAR(50),
    min_value FLOAT,
    max_value FLOAT,
    action ENUM('avoid', 'limit', 'safe') NOT NULL,
    priority ENUM('low', 'medium', 'high') DEFAULT 'medium',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (disease_id) REFERENCES diseases(id)
);
-- 6. Scan History Table (for tracking user scans)
CREATE TABLE IF NOT EXISTS scan_history (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    food_id INT NOT NULL,
    verdict VARCHAR(50),
    score INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (food_id) REFERENCES food(id)
);
-- 7. Alternatives Table
CREATE TABLE IF NOT EXISTS alternatives (
    id INT PRIMARY KEY AUTO_INCREMENT,
    food_id INT NOT NULL,
    alternative_food_id INT NOT NULL,
    rating FLOAT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (food_id) REFERENCES food(id),
    FOREIGN KEY (alternative_food_id) REFERENCES food(id)
);
-- Insert Sample Diseases
INSERT INTO diseases (disease_name, description)
VALUES (
        'Diabetes',
        'Blood sugar management - requires low sugar, low GI foods'
    ),
    (
        'PCOD',
        'Polycystic Ovarian Disease - requires high protein, low refined carbs'
    ),
    (
        'Thyroid',
        'Thyroid disorder - requires balanced iodine, avoid excessive soy'
    ),
    (
        'Obesity',
        'Weight management - requires low calorie, high fiber foods'
    ),
    (
        'BP',
        'High Blood Pressure - requires low sodium, heart-healthy foods'
    );
-- Insert Sample Foods
INSERT INTO food (name, barcode, description, category)
VALUES (
        'Regular Biscuits',
        '8901002020020',
        'Packaged biscuits with sugar',
        'Snacks'
    ),
    (
        'Whole Wheat Bread',
        '8901234567890',
        'Healthy whole wheat bread',
        'Bakery'
    ),
    (
        'Refined Flour',
        '8901111222333',
        'All-purpose refined flour',
        'Grains'
    ),
    (
        'Regular Cola',
        '8901444555666',
        'Carbonated soft drink',
        'Beverages'
    ),
    (
        'Oats Cereal',
        '8901777888999',
        'Whole grain oats',
        'Cereals'
    );
-- Insert Nutrition Data
INSERT INTO nutrition (
        food_id,
        calories,
        sugar,
        carbs,
        protein,
        fat,
        saturated_fat,
        fiber,
        sodium
    )
VALUES (1, 480, 12, 65, 6, 20, 8, 2, 200),
    (2, 265, 3, 49, 9, 3, 1, 7, 400),
    (3, 364, 0, 76, 10, 1, 0.2, 2, 2),
    (4, 140, 39, 39, 0, 0, 0, 0, 40),
    (5, 150, 1, 27, 5, 3, 0.5, 4, 100);
-- Insert Disease Rules for Diabetes
INSERT INTO disease_rules (
        disease_id,
        nutrient,
        max_value,
        action,
        priority
    )
VALUES (1, 'sugar', 5, 'avoid', 'high'),
    (1, 'refined_carbs', 30, 'avoid', 'high'),
    (1, 'glycemic_index', 55, 'avoid', 'high'),
    (1, 'fiber', 5, 'safe', 'medium');
-- Insert Disease Rules for PCOD
INSERT INTO disease_rules (
        disease_id,
        nutrient,
        min_value,
        action,
        priority
    )
VALUES (2, 'protein', 25, 'safe', 'high'),
    (2, 'refined_carbs', 35, 'avoid', 'high'),
    (2, 'sugar', 5, 'avoid', 'high'),
    (2, 'fiber', 8, 'safe', 'medium');
-- Insert Disease Rules for Thyroid
INSERT INTO disease_rules (
        disease_id,
        nutrient,
        min_value,
        action,
        priority
    )
VALUES (3, 'iodine', 100, 'safe', 'high'),
    (3, 'selenium', 50, 'safe', 'high');
-- Insert Disease Rules for Obesity
INSERT INTO disease_rules (
        disease_id,
        nutrient,
        max_value,
        action,
        priority
    )
VALUES (4, 'calories', 300, 'avoid', 'high'),
    (4, 'fat', 10, 'avoid', 'high'),
    (4, 'sugar', 3, 'avoid', 'high'),
    (4, 'fiber', 5, 'safe', 'medium');
-- Insert Disease Rules for BP
INSERT INTO disease_rules (
        disease_id,
        nutrient,
        max_value,
        action,
        priority
    )
VALUES (5, 'sodium', 200, 'avoid', 'high'),
    (5, 'saturated_fat', 2, 'avoid', 'high');