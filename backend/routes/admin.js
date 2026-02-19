const express = require('express');
const { authenticateToken } = require('../middleware/auth');
const router = express.Router();

// Add new food (admin only)
router.post('/food/add', authenticateToken, async (req, res) => {
    const { name, barcode, calories, sugar, carbs, protein, fat, fiber, sodium } = req.body;

    try {
        // In production, check if user is admin
        // For now, we'll just validate the input
        if (!name || !barcode) {
            return res.status(400).json({ error: 'Name and barcode are required' });
        }

        // Add to database (in production, use actual DB)
        res.json({
            success: true,
            message: 'Food added successfully',
            food: { name, barcode, calories, sugar, carbs, protein, fat, fiber, sodium }
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// Update disease rules (admin only)
router.put('/disease-rules/:disease', authenticateToken, async (req, res) => {
    const { disease } = req.params;
    const rules = req.body;

    try {
        res.json({
            success: true,
            message: `Rules for ${disease} updated successfully`,
            rules
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// Get user statistics (admin dashboard)
router.get('/stats', authenticateToken, async (req, res) => {
    try {
        res.json({
            total_users: 1250,
            total_foods: 5000,
            daily_scans: 450,
            diseases_tracked: ['Diabetes', 'PCOD', 'Thyroid', 'Obesity', 'BP']
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

module.exports = router;
