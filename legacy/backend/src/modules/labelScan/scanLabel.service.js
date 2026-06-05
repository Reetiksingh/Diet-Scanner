const logger = require('../../utils/logger');
const { parseNutritionText } = require('../../utils/nutritionParser');
const { createSha256Hash } = require('../../utils/hash');
const { getCache, setCache } = require('../../config/redisClient');
const { evaluateNutritionDecision } = require('../decisionEngine/decision.service');
const {
    startProcessingTimer,
    endProcessingTimer,
    recordCacheHit,
    recordCacheMiss,
    recordProcessedJob,
    buildApiMetrics
} = require('../../utils/metrics');

const CACHE_TTL_SECONDS = 60 * 60;
const CACHE_PREFIX = 'labelScan:parsed';
const JOB_RESULT_CACHE_PREFIX = 'labelScan:result';

let tesseractLibrary;
let tesseractLoadAttempted = false;

const getTesseractLibrary = () => {
    if (tesseractLoadAttempted) {
        return tesseractLibrary;
    }

    tesseractLoadAttempted = true;

    try {
        tesseractLibrary = require('tesseract.js');
    } catch (error) {
        tesseractLibrary = null;
        logger.warn('tesseract.js is not installed. OCR endpoint will return an error until installed.');
    }

    return tesseractLibrary;
};

const buildImageCacheKey = (imageHash) => `${CACHE_PREFIX}:image:${imageHash}`;
const buildRawTextCacheKey = (rawTextHash) => `${CACHE_PREFIX}:rawText:${rawTextHash}`;

const extractTextFromImage = async (imageBuffer) => {
    const tesseract = getTesseractLibrary();
    if (!tesseract) {
        throw new Error('OCR dependency missing. Please install tesseract.js in backend dependencies.');
    }

    const ocrTimer = startProcessingTimer();

    const result = await tesseract.recognize(imageBuffer, 'eng');
    const ocrTimeMs = endProcessingTimer(ocrTimer);

    logger.info('OCR completed.', { ocrProcessingTimeMs: ocrTimeMs });

    return {
        rawText: (result?.data?.text || '').trim(),
        ocrTimeMs
    };
};

const parseCachedPayload = (cachedValue) => {
    if (!cachedValue) {
        return null;
    }

    try {
        return JSON.parse(cachedValue);
    } catch (error) {
        return null;
    }
};

const buildJobResultCacheKey = (jobId) => `${JOB_RESULT_CACHE_PREFIX}:${jobId}`;

const getCachedNutritionResultByImageHash = async (imageHash) => {
    if (!imageHash) {
        return null;
    }

    const cached = await getCache(buildImageCacheKey(imageHash));
    return parseCachedPayload(cached);
};

const cacheNutritionResult = async ({ imageHash, rawText, payload }) => {
    const rawTextHash = createSha256Hash(rawText || '');

    await setCache(buildImageCacheKey(imageHash), JSON.stringify(payload), CACHE_TTL_SECONDS);
    await setCache(buildRawTextCacheKey(rawTextHash), JSON.stringify(payload), CACHE_TTL_SECONDS);
};

const getCachedScanResultByJobId = async (jobId) => {
    if (!jobId) {
        return null;
    }

    const cached = await getCache(buildJobResultCacheKey(jobId));
    return parseCachedPayload(cached);
};

const cacheScanResultByJobId = async (jobId, payload) => {
    if (!jobId) {
        return;
    }

    await setCache(buildJobResultCacheKey(jobId), JSON.stringify(payload), CACHE_TTL_SECONDS);
};

const processImageForNutrition = async ({ imageBuffer, imageHash, userProfile }) => {
    const timer = startProcessingTimer();

    const cachedByImage = await getCachedNutritionResultByImageHash(imageHash);
    if (cachedByImage) {
        recordCacheHit();
        logger.info('Label scan cache hit by image hash.', { imageHash });

        const processingTimeMs = endProcessingTimer(timer);
        const metrics = buildApiMetrics(processingTimeMs, 'HIT');

        return {
            ...cachedByImage,
            ...metrics
        };
    }

    recordCacheMiss();
    logger.info('Label scan cache miss by image hash.', { imageHash });

    const { rawText, ocrTimeMs } = await extractTextFromImage(imageBuffer);
    const rawTextHash = createSha256Hash(rawText || '');

    const cachedByRawText = parseCachedPayload(await getCache(buildRawTextCacheKey(rawTextHash)));
    if (cachedByRawText) {
        recordCacheHit();
        logger.info('Label scan cache hit by raw text hash.', { rawTextHash });

        await setCache(buildImageCacheKey(imageHash), JSON.stringify(cachedByRawText), CACHE_TTL_SECONDS);

        const processingTimeMs = endProcessingTimer(timer);
        const metrics = buildApiMetrics(processingTimeMs, 'HIT');

        return {
            ...cachedByRawText,
            ...metrics
        };
    }

    const parsedNutrition = parseNutritionText(rawText);
    const decision = evaluateNutritionDecision(parsedNutrition, userProfile);

    const payload = {
        rawText,
        nutrition: parsedNutrition,
        decision,
        ocrProcessingTime: `${ocrTimeMs}ms`
    };

    await cacheNutritionResult({ imageHash, rawText, payload });

    const processingTimeMs = endProcessingTimer(timer);
    recordProcessedJob(processingTimeMs);

    const metrics = buildApiMetrics(processingTimeMs, 'MISS');

    return {
        ...payload,
        ...metrics
    };
};

module.exports = {
    CACHE_TTL_SECONDS,
    processImageForNutrition,
    getCachedNutritionResultByImageHash,
    getCachedScanResultByJobId,
    cacheScanResultByJobId
};
