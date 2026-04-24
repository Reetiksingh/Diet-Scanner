const NUTRIENT_PATTERNS = {
    calories: ['calories', 'calorie', 'energy', 'kcal'],
    sugar: ['total sugar', 'sugars', 'sugar', 'sugr'],
    fat: ['total fat', 'fat', 'fats'],
    protein: ['protein', 'protien', 'prot'],
    sodium: ['sodium', 'salt']
};

const escapePattern = (value) => value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');

const roundTo = (value, precision = 2) => Number(Number(value || 0).toFixed(precision));

const normalizeCalories = (value) => roundTo(value, 0);

const normalizeToGrams = (value, unit) => {
    if (!Number.isFinite(value)) {
        return 0;
    }

    if (!unit) {
        return roundTo(value);
    }

    if (unit.toLowerCase() === 'mg') {
        return roundTo(value / 1000);
    }

    return roundTo(value);
};

const normalizeToSodiumMg = (value, unit) => {
    if (!Number.isFinite(value)) {
        return 0;
    }

    if (!unit) {
        return roundTo(value);
    }

    if (unit.toLowerCase() === 'g') {
        return roundTo(value * 1000);
    }

    return roundTo(value);
};

const extractNutrientValue = (rawText, aliases = []) => {
    if (!rawText || !aliases.length) {
        return null;
    }

    for (const alias of aliases) {
        const regex = new RegExp(
            `${escapePattern(alias)}\\s*(?:\\([^)]*\\))?\\s*[:\\-]?\\s*(\\d+(?:[.,]\\d+)?)\\s*(kcal|cal|mg|g)?`,
            'i'
        );

        const match = rawText.match(regex);
        if (match) {
            const value = Number(String(match[1]).replace(',', '.'));
            const unit = match[2] ? match[2].toLowerCase() : null;

            if (Number.isFinite(value)) {
                return { value, unit };
            }
        }
    }

    return null;
};

const parseNutritionText = (rawText = '') => {
    const normalizedText = String(rawText)
        .replace(/\r/g, '\n')
        .replace(/\t/g, ' ')
        .replace(/\s+/g, ' ')
        .trim()
        .toLowerCase();

    const caloriesMatch = extractNutrientValue(normalizedText, NUTRIENT_PATTERNS.calories);
    const sugarMatch = extractNutrientValue(normalizedText, NUTRIENT_PATTERNS.sugar);
    const fatMatch = extractNutrientValue(normalizedText, NUTRIENT_PATTERNS.fat);
    const proteinMatch = extractNutrientValue(normalizedText, NUTRIENT_PATTERNS.protein);
    const sodiumMatch = extractNutrientValue(normalizedText, NUTRIENT_PATTERNS.sodium);

    return {
        calories: caloriesMatch ? normalizeCalories(caloriesMatch.value) : 0,
        sugar: sugarMatch ? normalizeToGrams(sugarMatch.value, sugarMatch.unit) : 0,
        fat: fatMatch ? normalizeToGrams(fatMatch.value, fatMatch.unit) : 0,
        protein: proteinMatch ? normalizeToGrams(proteinMatch.value, proteinMatch.unit) : 0,
        sodium: sodiumMatch ? normalizeToSodiumMg(sodiumMatch.value, sodiumMatch.unit) : 0
    };
};

module.exports = {
    parseNutritionText
};
