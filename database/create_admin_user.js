/**
 * Script to create admin user
 * Run this after setting up the database
 * 
 * Usage: node database/create_admin_user.js
 */

const mysql = require('mysql2/promise');
const bcrypt = require('bcryptjs');
require('dotenv').config({ path: require('path').join(__dirname, '../backend/.env') });

async function createAdminUser() {
    let connection;
    
    try {
        connection = await mysql.createConnection({
            host: process.env.DB_HOST,
            user: process.env.DB_USER,
            password: process.env.DB_PASSWORD,
            database: process.env.DB_NAME
        });

        const adminEmail = 'admin@dietscanner.com';
        const adminPassword = 'admin123'; // CHANGE THIS IN PRODUCTION!
        const hashedPassword = await bcrypt.hash(adminPassword, 10);

        // Check if admin already exists
        const [existing] = await connection.execute(
            'SELECT id FROM users WHERE email = ?',
            [adminEmail]
        );

        if (existing.length > 0) {
            // Update existing user to admin
            await connection.execute(
                'UPDATE users SET role = ?, password = ? WHERE email = ?',
                ['admin', hashedPassword, adminEmail]
            );
            console.log('✅ Admin user updated successfully!');
        } else {
            // Create new admin user
            await connection.execute(
                `INSERT INTO users (username, email, password, disease, role) 
                 VALUES (?, ?, ?, ?, ?)`,
                ['Admin User', adminEmail, hashedPassword, 'diabetes', 'admin']
            );
            console.log('✅ Admin user created successfully!');
        }

        console.log('\n📧 Admin Credentials:');
        console.log('   Email: ' + adminEmail);
        console.log('   Password: ' + adminPassword);
        console.log('\n⚠️  IMPORTANT: Change the password in production!');

    } catch (error) {
        console.error('❌ Error creating admin user:', error.message);
        process.exit(1);
    } finally {
        if (connection) {
            await connection.end();
        }
    }
}

createAdminUser();
