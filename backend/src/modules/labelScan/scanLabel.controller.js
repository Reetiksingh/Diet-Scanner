const logger = require('../../utils/logger');
const { createSha256Hash } = require('../../utils/hash');
const {
    createJobId,
    enqueueLabelScanJob,
} = require('./labelScan.queue');
const {
    processImageForNutrition,
    getCachedScanResultByJobId,
    cacheScanResultByJobId
} = require('./scanLabel.service');
const {
    createPendingScanResult,
    updateCompletedScanResult,
    updateFailedScanResult,
    getScanResultByJobId
} = require('./scanResult.repository');

const extractUserProfile = (req) => {
    const diseaseFromBody = req.body?.disease || req.body?.condition;

    if (diseaseFromBody) {
        return {
            disease: diseaseFromBody
        };
    }

    if (req.user?.disease) {
        return {
            disease: req.user.disease
        };
    }

    return {
        conditions: []
    };
};

const isSyncModeEnabled = (req) => {
    const syncFlag = req.query?.sync || req.body?.sync;
    return String(syncFlag || '').toLowerCase() === 'true';
};

const isRawOnlyModeEnabled = (req) => {
    const rawOnlyFlag = req.query?.rawOnly || req.body?.rawOnly;
    return String(rawOnlyFlag || '').toLowerCase() === 'true';
};

const parseStoredJson = (value) => {
    if (!value) {
        return {};
    }

    if (Buffer.isBuffer(value)) {
        try {
            return JSON.parse(value.toString('utf-8'));
        } catch (error) {
            return {};
        }
    }

    if (typeof value === 'object') {
        return value;
    }

    try {
        return JSON.parse(value);
    } catch (error) {
        return {};
    }
};

const buildParsedDataForStorage = (result) => ({
    nutrition: result.nutrition || {},
    reasons: result.decision?.reasons || [],
    appliedProfiles: result.decision?.appliedProfiles || [],
    processingTime: result.processingTime || '0ms',
    ocrProcessingTime: result.ocrProcessingTime || null,
    cache: result.cache || 'MISS'
});

const buildCompletedPayload = ({ jobId, rawText, parsedData, decision }) => ({
    jobId,
    status: 'completed',
    rawText: rawText || '',
    nutrition: parsedData.nutrition || {},
    decision: {
        status: decision || 'SAFE',
        reasons: parsedData.reasons || []
    },
    processingTime: parsedData.processingTime || '0ms',
    cache: parsedData.cache || 'MISS',
    ocrProcessingTime: parsedData.ocrProcessingTime || null
});

const scanLabel = async (req, res) => {
    let jobId = null;

    try {
        if (!req.file || !req.file.buffer) {
            return res.status(400).json({ error: 'Image file is required as multipart/form-data with field name "image".' });
        }

        const imageBuffer = req.file.buffer;
        const imageHash = createSha256Hash(imageBuffer);
        const userProfile = extractUserProfile(req);
        const userId = req.user?.id || null;
        jobId = createJobId();

        logger.info('Scan label request received.', {
            fileSize: req.file.size,
            mimetype: req.file.mimetype,
            imageHash,
            jobId
        });

        await createPendingScanResult({
            jobId,
            userId
        });

        if (isSyncModeEnabled(req)) {
            const result = await processImageForNutrition({
                imageBuffer,
                imageHash,
                userProfile
            });

            const parsedData = buildParsedDataForStorage(result);
            const completedPayload = buildCompletedPayload({
                jobId,
                rawText: result.rawText,
                parsedData,
                decision: result.decision?.status
            });

            await updateCompletedScanResult({
                jobId,
                rawText: result.rawText,
                parsedData,
                decision: result.decision?.status || 'SAFE'
            });

            await cacheScanResultByJobId(jobId, completedPayload);

            if (isRawOnlyModeEnabled(req)) {
                return res.json({
                    rawText: result.rawText || '',
                    jobId
                });
            }

            return res.json(completedPayload);
        }

        const jobPayload = {
            imageBase64: imageBuffer.toString('base64'),
            imageHash,
            userProfile,
            jobId
        };

        await enqueueLabelScanJob(jobPayload, jobId);

        return res.status(202).json({
            jobId,
            status: 'processing'
        });
    } catch (error) {
        if (jobId) {
            try {
                await updateFailedScanResult({
                    jobId,
                    errorMessage: error.message
                });
            } catch (updateError) {
                logger.error('Failed to update scan result after request error.', {
                    jobId,
                    error: updateError.message
                });
            }
        }

        logger.error('Failed to process scan-label request.', { error: error.message });
        return res.status(500).json({ error: error.message });
    }
};

const getScanResult = async (req, res) => {
    try {
        const { jobId } = req.params;
        const cachedResult = await getCachedScanResultByJobId(jobId);

        if (cachedResult) {
            return res.json(cachedResult);
        }

        const scanResult = await getScanResultByJobId(jobId);

        if (!scanResult) {
            return res.status(404).json({ error: 'Scan job not found.' });
        }

        const parsedData = parseStoredJson(scanResult.parsed_data);

        if (scanResult.status === 'pending') {
            return res.json({
                jobId,
                status: 'pending'
            });
        }

        if (scanResult.status === 'failed') {
            return res.json({
                jobId,
                status: 'failed',
                error: parsedData.error || 'OCR processing failed.'
            });
        }

        const completedPayload = buildCompletedPayload({
            jobId,
            rawText: scanResult.raw_text,
            parsedData,
            decision: scanResult.decision
        });

        await cacheScanResultByJobId(jobId, completedPayload);

        return res.json(completedPayload);
    } catch (error) {
        logger.error('Failed to fetch scan result.', { error: error.message });
        return res.status(500).json({ error: error.message });
    }
};

module.exports = {
    scanLabel,
    getScanResult
};
