const express = require('express');
const pool = require('../db');
const foodDatabase = require('../utils/foodDatabase');
const { authenticateToken } = require('../middleware/auth');
const router = express.Router();

// Search food by barcode or barcode_id
router.get('/search/:barcode', authenticateToken, async (req, res) => {
    const { barcode } = req.params;

    try {
        const connection = await pool.getConnection();

        // Search in MySQL database by barcode or barcode_id
        const [foods] = await connection.execute(
            `SELECT f.*, 
                    n.calories, n.sugar, n.carbs, n.protein, n.fat, 
                    n.saturated_fat, n.fiber, n.sodium, n.potassium, n.iron, n.calcium
             FROM food f
             LEFT JOIN nutrition n ON f.id = n.food_id
             WHERE f.barcode = ? OR f.barcode_id = ?`,
            [barcode, barcode]
        );

        connection.release();

        if (foods.length > 0) {
            const food = foods[0];
            // Format food data for recommendation engine
            const formattedFood = {
                id: food.id,
                name: food.name,
                barcode: food.barcode,
                barcode_id: food.barcode_id,
                calories: food.calories || 0,
                sugar: food.sugar || 0,
                carbs: food.carbs || 0,
                protein: food.protein || 0,
                fat: food.fat || 0,
                fiber: food.fiber || 0,
                sodium: food.sodium || 0,
                alternatives: [] // Can be populated from alternatives table if needed
            };
            return res.json({ success: true, food: formattedFood });
        }

        // Fallback to local food database
        const localFood = foodDatabase.find(f => f.barcode === barcode);
        if (localFood) {
            return res.json({ success: true, food: localFood });
        }

        res.json({ success: false, message: 'Food not found in database' });
    } catch (error) {
        console.error('Error searching food:', error);
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
