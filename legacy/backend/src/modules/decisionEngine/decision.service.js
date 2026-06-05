const fs = require('fs');
const path = require('path');
const logger = require('../../utils/logger');

const RULES_FILE_PATH = path.join(__dirname, '../../config/healthRules.json');

const CONDITION_ALIASES = {
    bp: 'hypertension',
    high_blood_pressure: 'hypertension',
    'high-blood-pressure': 'hypertension',
    diabetic: 'diabetes'
};

let cachedRules = null;

const loadRules = () => {
    if (cachedRules) {
        return cachedRules;
    }

    const fileData = fs.readFileSync(RULES_FILE_PATH, 'utf-8');
    cachedRules = JSON.parse(fileData);
    return cachedRules;
};

const normalizeCondition = (condition) => {
    const normalized = String(condition || '').trim().toLowerCase();
    return CONDITION_ALIASES[normalized] || normalized;
};

const normalizeProfileConditions = (userProfile = {}) => {
    if (!userProfile) {
        return [];
    }

    if (Array.isArray(userProfile.conditions)) {
        return userProfile.conditions.map(normalizeCondition).filter(Boolean);
    }

    if (userProfile.disease) {
        return [normalizeCondition(userProfile.disease)].filter(Boolean);
    }

    if (userProfile.condition) {
        return [normalizeCondition(userProfile.condition)].filter(Boolean);
    }

    return [];
};

const evaluateRuleGroup = (rules, nutritionData, outputReasons = []) => {
    if (!rules || typeof rules !== 'object') {
        return;
    }

    Object.entries(rules).forEach(([nutrientKey, rule]) => {
        if (!rule || typeof rule !== 'object') {
            return;
        }

        const value = Number(nutritionData[nutrientKey] || 0);

        if (Number.isFinite(rule.max) && value > rule.max) {
            outputReasons.push({
                status: rule.statusOnFail || 'MODERATE',
                message: rule.reason || `${nutrientKey} exceeds limit (${value} > ${rule.max})`
            });
            return;
        }

        if (Number.isFinite(rule.min) && value < rule.min) {
            outputReasons.push({
                status: rule.statusOnFail || 'MODERATE',
                message: rule.reason || `${nutrientKey} is below expected minimum (${value} < ${rule.min})`
            });
        }
    });
};

const evaluateNutritionDecision = (nutritionData = {}, userProfile = {}) => {
    const rules = loadRules();
    const profileConditions = normalizeProfileConditions(userProfile);

    const reasons = [];
    evaluateRuleGroup(rules.general, nutritionData, reasons);

    profileConditions.forEach((condition) => {
        if (rules[condition]) {
            evaluateRuleGroup(rules[condition], nutritionData, reasons);
        }
    });

    const avoidStatus = rules.classification?.avoidStatus || 'AVOID';
    const moderateStatus = rules.classification?.moderateStatus || 'MODERATE';
    const safeStatus = rules.classification?.safeStatus || 'SAFE';

    const hasAvoidReason = reasons.some((reason) => reason.status === avoidStatus);

    let status = safeStatus;
    if (hasAvoidReason) {
        status = avoidStatus;
    } else if (reasons.length > 0) {
        status = moderateStatus;
    }

    const response = {
        status,
        reasons: reasons.map((reason) => reason.message),
        appliedProfiles: profileConditions.length ? profileConditions : ['general']
    };

    logger.info('Decision output generated.', {
        status: response.status,
        reasons: response.reasons,
        appliedProfiles: response.appliedProfiles
    });

    return response;
};

module.exports = {
    evaluateNutritionDecision
};
