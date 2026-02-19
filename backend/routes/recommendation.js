const express = require('express');
const { authenticateToken } = require('../middleware/auth');
const { applyDiseaseRules } = require('../utils/diseaseRuleEngine');
const router = express.Router();

// Get recommendation based on food and disease
router.post('/check', authenticateToken, async (req, res) => {
    const { food, disease } = req.body;

    try {
        if (!food || !disease) {
            return res.status(400).json({ error: 'Food and disease are required' });
        }

        // Apply disease rules to food
        const result = applyDiseaseRules(
            {
                calories: food.calories,
                sugar: food.sugar,
                carbs: food.carbs,
                protein: food.protein,
                fat: food.fat,
                fiber: food.fiber,
                sodium: food.sodium,
                saturated_fat: food.fat / 2 // Estimate saturated fat
            },
            disease
        );

        res.json({
            food_name: food.name,
            verdict: result.verdict,
            score: result.score,
            disease_name: result.disease_name,
            violations: result.violations,
            warnings: result.warnings,
            nutrition: {
                calories: food.calories,
                sugar: `${food.sugar}g`,
                carbs: `${food.carbs}g`,
                protein: `${food.protein}g`,
                fat: `${food.fat}g`,
                fiber: `${food.fiber}g`,
                sodium: `${food.sodium}mg`
            },
            alternatives: food.alternatives || [],
            recommendation: generateRecommendation(result.verdict, disease)
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

function generateRecommendation(verdict, disease) {
    const recommendations = {
        diabetes: {
            '✅ Safe': 'This food is safe for your diabetes management. Enjoy it as part of balanced meals.',
            '⚠ Limit': 'This food can be consumed occasionally in small portions. Monitor portion sizes carefully.',
            '❌ Avoid': 'This food is high in sugar and carbs. Avoid it and choose healthier alternatives.'
        },
        pcod: {
            '✅ Safe': 'This food is good for PCOD management. Rich in nutrients your body needs.',
            '⚠ Limit': 'This food has refined carbs. Limit portions and pair with protein/fiber.',
            '❌ Avoid': 'This food triggers inflammation. Choose anti-inflammatory alternatives.'
        },
        thyroid: {
            '✅ Safe': 'This food supports thyroid health with essential nutrients.',
            '⚠ Limit': 'Consume in moderation to avoid thyroid function interference.',
            '❌ Avoid': 'This food contains goitrogens or excessive iodine. Avoid.'
        },
        obesity: {
            '✅ Safe': 'Low-calorie food. Perfect for weight management.',
            '⚠ Limit': 'Moderate calories. Include in balanced diet with portion control.',
            '❌ Avoid': 'Too high in calories/fat. Choose lower-calorie options.'
        },
        bp: {
            '✅ Safe': 'Low sodium, heart-healthy option.',
            '⚠ Limit': 'Moderate sodium. Consume occasionally.',
            '❌ Avoid': 'High in sodium/saturated fat. Avoid for BP management.'
        }
    };

    return (recommendations[disease.toLowerCase()] || recommendations['diabetes'])[verdict];
}

module.exports = router;
