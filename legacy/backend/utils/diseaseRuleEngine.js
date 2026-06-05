// Disease Rule Engine - Core Logic

const diseaseRules = {
    diabetes: {
        name: 'Diabetes',
        rules: {
            sugar: { max: 5, priority: 'high' },
            carbs: { type: 'refined', max: 30, priority: 'high' },
            fiber: { min: 5, priority: 'medium' },
            glycemic_index: { max: 55, priority: 'high' }
        },
        keywords_avoid: ['sugar', 'refined', 'refined carbs', 'glucose', 'high GI'],
        keywords_limit: ['carbs', 'starch', 'white bread'],
        keywords_safe: ['fiber', 'whole grain', 'low GI', 'vegetable']
    },
    pcod: {
        name: 'PCOD (Polycystic Ovarian Disease)',
        rules: {
            protein: { min: 25, priority: 'high' },
            carbs: { type: 'refined', max: 35, priority: 'high' },
            sugar: { max: 5, priority: 'high' },
            fiber: { min: 8, priority: 'medium' },
            inflammatory: { avoid: true, priority: 'high' }
        },
        keywords_avoid: ['refined carbs', 'processed', 'sugar', 'high inflammatory'],
        keywords_limit: ['carbs', 'trans fat'],
        keywords_safe: ['protein', 'whole grain', 'fiber', 'antioxidants']
    },
    thyroid: {
        name: 'Thyroid',
        rules: {
            iodine: { min: 100, priority: 'high' },
            selenium: { required: true, priority: 'high' },
            soy: { max: 0, priority: 'high' },
            calories: { moderate: true, priority: 'medium' }
        },
        keywords_avoid: ['soy', 'excess iodine', 'raw cruciferous'],
        keywords_limit: ['processed salt', 'gluten'],
        keywords_safe: ['iodine', 'selenium', 'zinc', 'iron']
    },
    obesity: {
        name: 'Obesity',
        rules: {
            calories: { max: 300, priority: 'high' },
            fat: { max: 10, priority: 'high' },
            sugar: { max: 3, priority: 'high' },
            fiber: { min: 5, priority: 'medium' },
            protein: { min: 10, priority: 'medium' }
        },
        keywords_avoid: ['high calories', 'high fat', 'sugar', 'fried'],
        keywords_limit: ['carbs', 'salt', 'processed'],
        keywords_safe: ['low calorie', 'high fiber', 'protein', 'whole grain']
    },
    bp: {
        name: 'High Blood Pressure',
        rules: {
            sodium: { max: 200, priority: 'high' },
            potassium: { min: 150, priority: 'high' },
            saturated_fat: { max: 2, priority: 'high' },
            calories: { moderate: true, priority: 'medium' }
        },
        keywords_avoid: ['high salt', 'processed', 'fried', 'saturated fat'],
        keywords_limit: ['sodium', 'trans fat'],
        keywords_safe: ['low sodium', 'potassium rich', 'heart healthy']
    }
};

const applyDiseaseRules = (nutrition, disease) => {
    const rules = diseaseRules[disease.toLowerCase()];

    if (!rules) {
        return { verdict: '⚠ Limit', score: 50, reason: 'Disease not recognized' };
    }

    let score = 100;
    let violations = [];
    let warnings = [];

    // Check each rule
    if (rules.rules.sugar && nutrition.sugar > rules.rules.sugar.max) {
        score -= 30;
        violations.push(`Sugar content (${nutrition.sugar}g) exceeds safe limit (${rules.rules.sugar.max}g)`);
    }

    if (rules.rules.calories && nutrition.calories > 300 && disease.toLowerCase() === 'obesity') {
        score -= 20;
        warnings.push(`Calories (${nutrition.calories} kcal) is moderate to high`);
    }

    if (rules.rules.sodium && nutrition.sodium > rules.rules.sodium.max) {
        score -= 25;
        violations.push(`Sodium (${nutrition.sodium}mg) exceeds safe limit`);
    }

    if (rules.rules.fat && nutrition.saturated_fat > rules.rules.fat.max) {
        score -= 20;
        violations.push(`Fat content exceeds recommendations`);
    }

    // Determine verdict based on score
    let verdict = '✅ Safe';
    if (violations.length > 2 || score < 30) {
        verdict = '❌ Avoid';
    } else if (violations.length > 0 || score < 60) {
        verdict = '⚠ Limit';
    }

    return {
        verdict,
        score,
        violations,
        warnings,
        disease_name: rules.name
    };
};

module.exports = { diseaseRules, applyDiseaseRules };
