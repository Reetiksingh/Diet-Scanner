const logger = require('../utils/logger');

let Redis = null;
try {
    Redis = require('ioredis');
} catch (error) {
    logger.warn('ioredis not installed. Falling back to in-memory cache.');
}

const MEMORY_STORE = new Map();
let redisClient = null;
let redisReady = false;
let redisErrorLogged = false;
const hasRedisConnectionConfig = Boolean(process.env.REDIS_HOST || process.env.REDIS_URL);

const redisConfig = {
    host: process.env.REDIS_HOST || '127.0.0.1',
    port: Number(process.env.REDIS_PORT || 6379),
    password: process.env.REDIS_PASSWORD || undefined,
    db: Number(process.env.REDIS_DB || 0),
    maxRetriesPerRequest: 1,
    enableOfflineQueue: false,
    lazyConnect: true,
    connectTimeout: 1000,
    retryStrategy: () => null
};

const isRedisDisabled = String(process.env.REDIS_DISABLED || '').toLowerCase() === 'true';

if (Redis && !isRedisDisabled && hasRedisConnectionConfig) {
    redisClient = process.env.REDIS_URL ? new Redis(process.env.REDIS_URL, redisConfig) : new Redis(redisConfig);

    redisClient.on('ready', () => {
        redisReady = true;
        redisErrorLogged = false;
        logger.info('Redis cache client connected.');
    });

    redisClient.on('error', (error) => {
        redisReady = false;
        if (!redisErrorLogged) {
            redisErrorLogged = true;
            logger.warn('Redis cache client error. Using in-memory fallback.', { error: error.message });
        }
    });

    redisClient.connect().catch((error) => {
        redisReady = false;
        logger.warn('Redis connection failed at startup. Using in-memory fallback.', { error: error.message });
    });
} else if (!hasRedisConnectionConfig) {
    logger.info('Redis configuration not found. Using in-memory cache fallback.');
}

const getMemoryEntry = (key) => {
    const entry = MEMORY_STORE.get(key);

    if (!entry) {
        return null;
    }

    if (entry.expiresAt && entry.expiresAt < Date.now()) {
        MEMORY_STORE.delete(key);
        return null;
    }

    return entry.value;
};

const setMemoryEntry = (key, value, ttlSeconds) => {
    const expiresAt = ttlSeconds ? Date.now() + ttlSeconds * 1000 : null;
    MEMORY_STORE.set(key, { value, expiresAt });
};

const getCache = async (key) => {
    if (redisClient && redisReady) {
        try {
            return await redisClient.get(key);
        } catch (error) {
            logger.warn('Redis get failed, fallback to memory.', { key, error: error.message });
        }
    }

    return getMemoryEntry(key);
};

const setCache = async (key, value, ttlSeconds) => {
    if (redisClient && redisReady) {
        try {
            if (ttlSeconds) {
                await redisClient.set(key, value, 'EX', ttlSeconds);
                return;
            }

            await redisClient.set(key, value);
            return;
        } catch (error) {
            logger.warn('Redis set failed, fallback to memory.', { key, error: error.message });
        }
    }

    setMemoryEntry(key, value, ttlSeconds);
};

const deleteCache = async (key) => {
    if (redisClient && redisReady) {
        try {
            await redisClient.del(key);
            return;
        } catch (error) {
            logger.warn('Redis delete failed, fallback to memory.', { key, error: error.message });
        }
    }

    MEMORY_STORE.delete(key);
};

const isRedisActive = () => Boolean(redisClient && redisReady);

module.exports = {
    getCache,
    setCache,
    deleteCache,
    isRedisActive,
    redisConfig
};
