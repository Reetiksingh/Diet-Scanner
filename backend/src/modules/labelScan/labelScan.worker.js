const logger = require('../../utils/logger');
const {
    registerMemoryProcessor,
    setJobState,
    getQueueRuntime
} = require('./labelScan.queue');
const {
    processImageForNutrition,
    cacheScanResultByJobId
} = require('./scanLabel.service');
const {
    updateCompletedScanResult,
    updateFailedScanResult
} = require('./scanResult.repository');

let workerInstance = null;
let workerInitialized = false;

const buildParsedDataForStorage = (result) => ({
    nutrition: result.nutrition || {},
    reasons: result.decision?.reasons || [],
    appliedProfiles: result.decision?.appliedProfiles || [],
    processingTime: result.processingTime || '0ms',
    ocrProcessingTime: result.ocrProcessingTime || null,
    cache: result.cache || 'MISS'
});

const buildCompletedPayload = (jobId, result) => ({
    jobId,
    status: 'completed',
    rawText: result.rawText || '',
    nutrition: result.nutrition || {},
    decision: {
        status: result.decision?.status || 'SAFE',
        reasons: result.decision?.reasons || []
    },
    processingTime: result.processingTime || '0ms',
    cache: result.cache || 'MISS',
    ocrProcessingTime: result.ocrProcessingTime || null
});

const processLabelScanJob = async (job) => {
    const jobId = job?.data?.jobId || job?.id;

    if (!jobId) {
        return;
    }

    await setJobState(jobId, {
        status: 'processing'
    });

    try {
        const imageBase64 = job?.data?.imageBase64 || '';
        const imageHash = job?.data?.imageHash;
        const userProfile = job?.data?.userProfile || {};

        const result = await processImageForNutrition({
            imageBuffer: Buffer.from(imageBase64, 'base64'),
            imageHash,
            userProfile
        });

        const parsedData = buildParsedDataForStorage(result);
        const completedPayload = buildCompletedPayload(jobId, result);

        await updateCompletedScanResult({
            jobId,
            rawText: result.rawText,
            parsedData,
            decision: result.decision?.status || 'SAFE'
        });

        await cacheScanResultByJobId(jobId, completedPayload);

        await setJobState(jobId, {
            status: 'completed',
            result: completedPayload
        });

        logger.info('Label scan job completed.', { jobId, status: result.decision?.status });

        return completedPayload;
    } catch (error) {
        try {
            await updateFailedScanResult({
                jobId,
                errorMessage: error.message
            });
        } catch (updateError) {
            logger.error('Failed to persist failed scan result.', {
                jobId,
                error: updateError.message
            });
        }

        await setJobState(jobId, {
            status: 'failed',
            error: error.message
        });

        logger.error('Label scan job failed.', { jobId, error: error.message });
        throw error;
    }
};

const initializeLabelScanWorker = () => {
    if (workerInitialized) {
        return workerInstance;
    }

    workerInitialized = true;

    const runtime = getQueueRuntime();

    if (runtime.hasBullMq && runtime.Worker) {
        workerInstance = new runtime.Worker(runtime.queueName, processLabelScanJob, {
            connection: runtime.connection
        });

        workerInstance.on('completed', (job) => {
            logger.info('BullMQ worker marked job completed.', { jobId: job.id });
        });

        workerInstance.on('failed', (job, error) => {
            logger.error('BullMQ worker marked job failed.', {
                jobId: job?.id,
                error: error?.message
            });
        });

        logger.info('BullMQ worker initialized for label scanning.');
        return workerInstance;
    }

    registerMemoryProcessor(processLabelScanJob);
    logger.info('In-memory async worker initialized for label scanning.');

    return null;
};

module.exports = {
    initializeLabelScanWorker
};
