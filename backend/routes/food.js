const express = require('express');
const pool = require('../db');
const foodDatabase = require('../utils/foodDatabase');
const { authenticateToken } = require('../middleware/auth');
const router = express.Router();

// Search food by barcode
router.get('/search/:barcode', authenticateToken, async (req, res) => {
    const { barcode } = req.params;

    try {
        // Search in local food database first
        const food = foodDatabase.find(f => f.barcode === barcode);

        if (food) {
            return res.json({ success: true, food });
        }

        // If not found, you could integrate with external API like Open Food Facts
        res.json({ success: false, message: 'Food not found in database' });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// Get food by ID
router.get('/:id', authenticateToken, async (req, res) => {
    const { id } = req.params;

    try {
        const food = foodDatabase.find(f => f.id == id);

        if (!food) {
            return res.status(404).json({ error: 'Food not found' });
        }

        res.json(food);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// Get all foods (for admin)
router.get('/', authenticateToken, async (req, res) => {
    try {
        res.json(foodDatabase);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

module.exports = router;
