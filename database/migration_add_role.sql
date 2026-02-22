-- Migration: Add role column to users table and create admin user
-- Run this after the main schema.sql

USE diet_scanner;

-- Add role column if it doesn't exist
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS role ENUM('admin', 'user') DEFAULT 'user' AFTER disease;

-- Add barcode_id column to food table if it doesn't exist
ALTER TABLE food 
ADD COLUMN IF NOT EXISTS barcode_id VARCHAR(100) UNIQUE AFTER barcode;

-- Create admin user (password: admin123 - CHANGE THIS IN PRODUCTION!)
-- Email: admin@dietscanner.com
-- Password hash for 'admin123' (bcrypt, rounds=10)
INSERT INTO users (username, email, password, disease, role) 
VALUES (
    'Admin User',
    'admin@dietscanner.com',
    '$2a$10$rOzJqZqZqZqZqZqZqZqZqOqZqZqZqZqZqZqZqZqZqZqZqZqZqZqZq',
    'diabetes',
    'admin'
) 
ON DUPLICATE KEY UPDATE role = 'admin';

-- Note: The password hash above is a placeholder. 
-- In production, generate a proper hash using bcrypt.hash('admin123', 10)
-- Or use the registration endpoint to create the admin user first, then update role manually
