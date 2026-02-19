const express = require('express');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const pool = require('../db');
const router = express.Router();

// Register
router.post('/register', async (req, res) => {
    const { username, email, password, disease } = req.body;

    try {
        const connection = await pool.getConnection();

        // Check if user exists
        const [existing] = await connection.execute('SELECT * FROM users WHERE email = ?', [email]);
        if (existing.length > 0) {
            connection.release();
            return res.status(400).json({ error: 'User already exists' });
        }

        // Hash password
        const hashedPassword = await bcrypt.hash(password, 10);

        // Insert user
        await connection.execute(
            'INSERT INTO users (username, email, password, disease) VALUES (?, ?, ?, ?)',
            [username, email, hashedPassword, disease]
        );

        connection.release();
        res.status(201).json({ message: 'User registered successfully' });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// Login
router.post('/login', async (req, res) => {
    const { email, password } = req.body;

    try {
        const connection = await pool.getConnection();

        const [users] = await connection.execute('SELECT * FROM users WHERE email = ?', [email]);

        if (users.length === 0) {
            connection.release();
            return res.status(401).json({ error: 'Invalid credentials' });
        }

        const user = users[0];
        const validPassword = await bcrypt.compare(password, user.password);

        if (!validPassword) {
            connection.release();
            return res.status(401).json({ error: 'Invalid credentials' });
        }

        const token = jwt.sign(
            { id: user.id, email: user.email, disease: user.disease },
            process.env.JWT_SECRET,
            { expiresIn: '7d' }
        );

        connection.release();
        res.json({ token, user: { id: user.id, username: user.username, disease: user.disease } });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

module.exports = router;
