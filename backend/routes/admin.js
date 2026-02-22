const express = require('express');
const { authenticateToken } = require('../middleware/auth');
const { adminMiddleware } = require('../middleware/admin');
const pool = require('../db');
const QRCode = require('qrcode');
const { v4: uuidv4 } = require('uuid');

const router = express.Router();

// All admin routes require authentication AND admin role
// Using authenticateToken first, then adminMiddleware

// ============ FOOD MANAGEMENT ============

/**
 * POST /admin/food
 * Add new food to database with QR code generation
 */
router.post('/food', authenticateToken, adminMiddleware, async (req, res) => {
    const { 
        name, 
        barcode, 
        description, 
        category, 
        brand,
        calories, 
        sugar, 
        carbs, 
        protein, 
        fat, 
        saturated_fat,
        fiber, 
        sodium,
        potassium,
        iron,
        calcium
    } = req.body;

    try {
        // Validate required fields
        if (!name || !barcode) {
            return res.status(400).json({ error: 'Name and barcode are required' });
        }

        const connection = await pool.getConnection();

        // Check if barcode already exists
        const [existing] = await connection.execute(
            'SELECT id FROM food WHERE barcode = ?',
            [barcode]
        );

        if (existing.length > 0) {
            connection.release();
            return res.status(400).json({ error: 'Food with this barcode already exists' });
        }

        // Generate unique barcode_id for QR code
        const barcode_id = `FOOD-${uuidv4()}`;

        // Insert food into database
        const [foodResult] = await connection.execute(
            `INSERT INTO food (name, barcode, barcode_id, description, category, brand) 
             VALUES (?, ?, ?, ?, ?, ?)`,
            [name, barcode, barcode_id, description || null, category || null, brand || null]
        );

        const foodId = foodResult.insertId;

        // Insert nutrition data
        await connection.execute(
            `INSERT INTO nutrition (
                food_id, calories, sugar, carbs, protein, fat, saturated_fat, 
                fiber, sodium, potassium, iron, calcium
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
            [
                foodId,
                calories || null,
                sugar || null,
                carbs || null,
                protein || null,
                fat || null,
                saturated_fat || null,
                fiber || null,
                sodium || null,
                potassium || null,
                iron || null,
                calcium || null
            ]
        );

        // Generate QR code as base64
        const qrCodeDataURL = await QRCode.toDataURL(barcode_id, {
            errorCorrectionLevel: 'H',
            type: 'image/png',
            width: 300
        });

        connection.release();

        res.status(201).json({
            success: true,
            message: 'Food added successfully',
            food: {
                id: foodId,
                name,
                barcode,
                barcode_id,
                qr_code: qrCodeDataURL
            }
        });
    } catch (error) {
        console.error('Error adding food:', error);
        res.status(500).json({ error: error.message });
    }
});

/**
 * GET /admin/foods
 * Get all foods with pagination
 */
router.get('/foods', authenticateToken, adminMiddleware, async (req, res) => {
    try {
        const { page = 1, limit = 50 } = req.query;
        const offset = (page - 1) * limit;

        const connection = await pool.getConnection();

        // Get total count
        const [countResult] = await connection.execute('SELECT COUNT(*) as total FROM food');
        const total = countResult[0].total;

        // Get foods with nutrition data
        const [foods] = await connection.execute(
            `SELECT f.*, 
                    n.calories, n.sugar, n.carbs, n.protein, n.fat, n.fiber, n.sodium
             FROM food f
             LEFT JOIN nutrition n ON f.id = n.food_id
             ORDER BY f.created_at DESC
             LIMIT ? OFFSET ?`,
            [parseInt(limit), parseInt(offset)]
        );

        connection.release();

        res.json({
            success: true,
            foods,
            pagination: {
                page: parseInt(page),
                limit: parseInt(limit),
                total,
                totalPages: Math.ceil(total / limit)
            }
        });
    } catch (error) {
        console.error('Error fetching foods:', error);
        res.status(500).json({ error: error.message });
    }
});

/**
 * GET /admin/food/:id
 * Get single food by ID with QR code
 */
router.get('/food/:id', authenticateToken, adminMiddleware, async (req, res) => {
    try {
        const { id } = req.params;
        const connection = await pool.getConnection();

        const [foods] = await connection.execute(
            `SELECT f.*, 
                    n.calories, n.sugar, n.carbs, n.protein, n.fat, 
                    n.saturated_fat, n.fiber, n.sodium, n.potassium, n.iron, n.calcium
             FROM food f
             LEFT JOIN nutrition n ON f.id = n.food_id
             WHERE f.id = ?`,
            [id]
        );

        if (foods.length === 0) {
            connection.release();
            return res.status(404).json({ error: 'Food not found' });
        }

        const food = foods[0];

        // Generate QR code if barcode_id exists
        let qr_code = null;
        if (food.barcode_id) {
            qr_code = await QRCode.toDataURL(food.barcode_id, {
                errorCorrectionLevel: 'H',
                type: 'image/png',
                width: 300
            });
        }

        connection.release();

        res.json({
            success: true,
            food: {
                ...food,
                qr_code
            }
        });
    } catch (error) {
        console.error('Error fetching food:', error);
        res.status(500).json({ error: error.message });
    }
});

/**
 * PUT /admin/food/:id
 * Update food information
 */
router.put('/food/:id', authenticateToken, adminMiddleware, async (req, res) => {
    try {
        const { id } = req.params;
        const {
            name, barcode, description, category, brand,
            calories, sugar, carbs, protein, fat, saturated_fat,
            fiber, sodium, potassium, iron, calcium
        } = req.body;

        const connection = await pool.getConnection();

        // Check if food exists
        const [existing] = await connection.execute('SELECT id FROM food WHERE id = ?', [id]);
        if (existing.length === 0) {
            connection.release();
            return res.status(404).json({ error: 'Food not found' });
        }

        // Check barcode uniqueness if barcode is being updated
        if (barcode) {
            const [duplicate] = await connection.execute(
                'SELECT id FROM food WHERE barcode = ? AND id != ?',
                [barcode, id]
            );
            if (duplicate.length > 0) {
                connection.release();
                return res.status(400).json({ error: 'Barcode already exists' });
            }
        }

        // Update food table
        const updateFields = [];
        const updateValues = [];

        if (name) { updateFields.push('name = ?'); updateValues.push(name); }
        if (barcode) { updateFields.push('barcode = ?'); updateValues.push(barcode); }
        if (description !== undefined) { updateFields.push('description = ?'); updateValues.push(description); }
        if (category) { updateFields.push('category = ?'); updateValues.push(category); }
        if (brand) { updateFields.push('brand = ?'); updateValues.push(brand); }

        if (updateFields.length > 0) {
            updateValues.push(id);
            await connection.execute(
                `UPDATE food SET ${updateFields.join(', ')} WHERE id = ?`,
                updateValues
            );
        }

        // Update nutrition table
        const nutritionFields = [];
        const nutritionValues = [];

        if (calories !== undefined) { nutritionFields.push('calories = ?'); nutritionValues.push(calories); }
        if (sugar !== undefined) { nutritionFields.push('sugar = ?'); nutritionValues.push(sugar); }
        if (carbs !== undefined) { nutritionFields.push('carbs = ?'); nutritionValues.push(carbs); }
        if (protein !== undefined) { nutritionFields.push('protein = ?'); nutritionValues.push(protein); }
        if (fat !== undefined) { nutritionFields.push('fat = ?'); nutritionValues.push(fat); }
        if (saturated_fat !== undefined) { nutritionFields.push('saturated_fat = ?'); nutritionValues.push(saturated_fat); }
        if (fiber !== undefined) { nutritionFields.push('fiber = ?'); nutritionValues.push(fiber); }
        if (sodium !== undefined) { nutritionFields.push('sodium = ?'); nutritionValues.push(sodium); }
        if (potassium !== undefined) { nutritionFields.push('potassium = ?'); nutritionValues.push(potassium); }
        if (iron !== undefined) { nutritionFields.push('iron = ?'); nutritionValues.push(iron); }
        if (calcium !== undefined) { nutritionFields.push('calcium = ?'); nutritionValues.push(calcium); }

        if (nutritionFields.length > 0) {
            // Check if nutrition record exists
            const [nutritionExists] = await connection.execute(
                'SELECT id FROM nutrition WHERE food_id = ?',
                [id]
            );

            if (nutritionExists.length > 0) {
                nutritionValues.push(id);
                await connection.execute(
                    `UPDATE nutrition SET ${nutritionFields.join(', ')} WHERE food_id = ?`,
                    nutritionValues
                );
            } else {
                nutritionValues.unshift(id);
                await connection.execute(
                    `INSERT INTO nutrition (food_id, ${nutritionFields.map(f => f.split(' = ')[0]).join(', ')}) 
                     VALUES (?, ${nutritionFields.map(() => '?').join(', ')})`,
                    nutritionValues
                );
            }
        }

        connection.release();

        res.json({
            success: true,
            message: 'Food updated successfully'
        });
    } catch (error) {
        console.error('Error updating food:', error);
        res.status(500).json({ error: error.message });
    }
});

/**
 * DELETE /admin/food/:id
 * Delete food and its nutrition data
 */
router.delete('/food/:id', authenticateToken, adminMiddleware, async (req, res) => {
    try {
        const { id } = req.params;
        const connection = await pool.getConnection();

        // Check if food exists
        const [existing] = await connection.execute('SELECT id FROM food WHERE id = ?', [id]);
        if (existing.length === 0) {
            connection.release();
            return res.status(404).json({ error: 'Food not found' });
        }

        // Delete nutrition data first (foreign key constraint)
        await connection.execute('DELETE FROM nutrition WHERE food_id = ?', [id]);

        // Delete food
        await connection.execute('DELETE FROM food WHERE id = ?', [id]);

        connection.release();

        res.json({
            success: true,
            message: 'Food deleted successfully'
        });
    } catch (error) {
        console.error('Error deleting food:', error);
        res.status(500).json({ error: error.message });
    }
});

/**
 * GET /admin/food/:id/qr
 * Get QR code image for food
 */
router.get('/food/:id/qr', authenticateToken, adminMiddleware, async (req, res) => {
    try {
        const { id } = req.params;
        const connection = await pool.getConnection();

        const [foods] = await connection.execute(
            'SELECT barcode_id FROM food WHERE id = ?',
            [id]
        );

        if (foods.length === 0 || !foods[0].barcode_id) {
            connection.release();
            return res.status(404).json({ error: 'Food not found or no barcode_id' });
        }

        const barcode_id = foods[0].barcode_id;
        connection.release();

        // Generate QR code as PNG buffer
        const qrBuffer = await QRCode.toBuffer(barcode_id, {
            errorCorrectionLevel: 'H',
            type: 'png',
            width: 300
        });

        res.setHeader('Content-Type', 'image/png');
        res.send(qrBuffer);
    } catch (error) {
        console.error('Error generating QR code:', error);
        res.status(500).json({ error: error.message });
    }
});

// ============ STATISTICS & HISTORY ============

/**
 * GET /admin/stats
 * Get admin dashboard statistics
 */
router.get('/stats', authenticateToken, adminMiddleware, async (req, res) => {
    try {
        const connection = await pool.getConnection();

        // Get total users
        const [userCount] = await connection.execute('SELECT COUNT(*) as total FROM users');
        const total_users = userCount[0].total;

        // Get total foods
        const [foodCount] = await connection.execute('SELECT COUNT(*) as total FROM food');
        const total_foods = foodCount[0].total;

        // Get daily scans (today)
        const [scanCount] = await connection.execute(
            'SELECT COUNT(*) as total FROM scan_history WHERE DATE(created_at) = CURDATE()'
        );
        const daily_scans = scanCount[0].total;

        // Get total scans
        const [totalScans] = await connection.execute('SELECT COUNT(*) as total FROM scan_history');
        const total_scans = totalScans[0].total;

        connection.release();

        res.json({
            success: true,
            total_users,
            total_foods,
            daily_scans,
            total_scans,
            diseases_tracked: ['Diabetes', 'PCOD', 'Thyroid', 'Obesity', 'BP']
        });
    } catch (error) {
        console.error('Error fetching stats:', error);
        res.status(500).json({ error: error.message });
    }
});

/**
 * GET /admin/history
 * Get scan history with pagination
 */
router.get('/history', authenticateToken, adminMiddleware, async (req, res) => {
    try {
        const { page = 1, limit = 50 } = req.query;
        const offset = (page - 1) * limit;

        const connection = await pool.getConnection();

        // Get total count
        const [countResult] = await connection.execute('SELECT COUNT(*) as total FROM scan_history');
        const total = countResult[0].total;

        // Get scan history with user and food info
        const [history] = await connection.execute(
            `SELECT sh.*, 
                    u.username, u.email, u.disease,
                    f.name as food_name, f.barcode
             FROM scan_history sh
             LEFT JOIN users u ON sh.user_id = u.id
             LEFT JOIN food f ON sh.food_id = f.id
             ORDER BY sh.created_at DESC
             LIMIT ? OFFSET ?`,
            [parseInt(limit), parseInt(offset)]
        );

        connection.release();

        res.json({
            success: true,
            history,
            pagination: {
                page: parseInt(page),
                limit: parseInt(limit),
                total,
                totalPages: Math.ceil(total / limit)
            }
        });
    } catch (error) {
        console.error('Error fetching history:', error);
        res.status(500).json({ error: error.message });
    }
});

// Legacy endpoint for backward compatibility
router.post('/food/add', authenticateToken, adminMiddleware, async (req, res) => {
    // Redirect to new endpoint
    req.url = '/food';
    router.handle(req, res);
});

module.exports = router;
