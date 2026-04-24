const logger = require('../../utils/logger');
const { setCache, getCache } = require('../../config/redisClient');

const QUEUE_NAME = 'labelScan';
const JOB_KEY_PREFIX = 'labelScan:job';
const JOB_TTL_SECONDS = 60 * 60;

let BullMq = null;
let Redis = null;
let queueInstance = null;
let queueConnection = null;
let memoryProcessor = null;
let queueErrorLogged = false;
const hasRedisConnectionConfig = Boolean(process.env.REDIS_HOST || process.env.REDIS_URL);

try {
    BullMq = require('bullmq');
} catch (error) {
    logger.warn('bullmq not installed. Label scan will use in-memory async queue fallback.');
}

try {
    Redis = require('ioredis');
} catch (error) {
    logger.warn('ioredis not installed for BullMQ. Label scan will use in-memory async queue fallback.');
}

const shouldUseBullMq = Boolean(
    BullMq
    && Redis
    && hasRedisConnectionConfig
    && String(process.env.BULLMQ_DISABLED || '').toLowerCase() !== 'true'
);

if (shouldUseBullMq) {
    try {
        const connectionConfig = {
            host: process.env.REDIS_HOST || '127.0.0.1',
            port: Number(process.env.REDIS_PORT || 6379),
            password: process.env.REDIS_PASSWORD || undefined,
            db: Number(process.env.REDIS_DB || 0),
            maxRetriesPerRequest: null,
            connectTimeout: 1000,
            lazyConnect: true,
            enableOfflineQueue: false,
            retryStrategy: () => null
        };

        queueConnection = process.env.REDIS_URL
            ? new Redis(process.env.REDIS_URL, connectionConfig)
            : new Redis(connectionConfig);

        queueConnection.on('error', (error) => {
            if (!queueErrorLogged) {
                queueErrorLogged = true;
                logger.warn('BullMQ Redis connection error. Switching to in-memory queue fallback.', {
                    error: error.message
                });
            }
        });

        queueInstance = new BullMq.Queue(QUEUE_NAME, {
            connection: queueConnection
        });

        logger.info('BullMQ queue initialized for label scanning.', { queue: QUEUE_NAME });
    } catch (error) {
        queueInstance = null;
        queueConnection = null;
        logger.warn('BullMQ queue initialization failed. Falling back to in-memory queue.', { error: error.message });
    }
} else if (!hasRedisConnectionConfig) {
    logger.info('BullMQ Redis config not found. Using in-memory async queue fallback.');
}

const buildJobKey = (jobId) => `${JOB_KEY_PREFIX}:${jobId}`;

const createJobId = () => `scan-${Date.now()}-${Math.random().toString(36).slice(2, 10)}`;

const setJobState = async (jobId, payload) => {
    const value = {
        ...payload,
        updatedAt: new Date().toISOString()
    };

    await setCache(buildJobKey(jobId), JSON.stringify(value), JOB_TTL_SECONDS);
};

const getJobState = async (jobId) => {
    const cachedValue = await getCache(buildJobKey(jobId));
    if (!cachedValue) {
        return null;
    }

    try {
        return JSON.parse(cachedValue);
    } catch (error) {
        return null;
    }
};

const registerMemoryProcessor = (processor) => {
    memoryProcessor = processor;
};

const enqueueLabelScanJob = async (payload, providedJobId) => {
    const jobId = providedJobId || payload?.jobId || createJobId();

    await setJobState(jobId, {
        status: 'QUEUED'
    });

    if (queueInstance) {
        try {
            await queueInstance.add(
                'scan-label',
                {
                    ...payload,
                    jobId
                },
                {
                    jobId,
                    removeOnComplete: true,
                    removeOnFail: true
                }
            );

            return {
                jobId,
                queueMode: 'bullmq'
            };
        } catch (error) {
            logger.warn('BullMQ enqueue failed. Falling back to in-memory queue.', { error: error.message });
            queueInstance = null;
            if (queueConnection) {
                queueConnection.disconnect();
                queueConnection = null;
            }
        }
    }

    if (typeof memoryProcessor === 'function') {
        setTimeout(() => {
            memoryProcessor({
                id: jobId,
                data: {
                    ...payload,
                    jobId
                }
            });
        }, 0);
    }

    return {
        jobId,
        queueMode: 'memory'
    };
};

const createCompletedJobFromCache = async (cachedResult) => {
    const jobId = createJobId();

    await setJobState(jobId, {
        status: 'COMPLETED',
        result: {
            ...cachedResult,
            cache: 'HIT',
            processingTime: cachedResult.processingTime || '0ms'
        }
    });

    return jobId;
};

const getQueueRuntime = () => ({
    hasBullMq: Boolean(queueInstance && queueConnection),
    Worker: BullMq ? BullMq.Worker : null,
    queueName: QUEUE_NAME,
    connection: queueConnection
});

module.exports = {
    createJobId,
    enqueueLabelScanJob,
    setJobState,
    getJobState,
    registerMemoryProcessor,
    createCompletedJobFromCache,
    getQueueRuntime
};
