const metricsState = {
    cacheHits: 0,
    cacheMisses: 0,
    processedJobs: 0,
    lastProcessingTimeMs: 0
};

const startProcessingTimer = () => process.hrtime.bigint();

const endProcessingTimer = (startTime) => {
    if (!startTime) {
        return 0;
    }

    const elapsed = process.hrtime.bigint() - startTime;
    return Number(elapsed / 1000000n);
};

const formatProcessingTime = (processingTimeMs) => `${processingTimeMs}ms`;

const recordCacheHit = () => {
    metricsState.cacheHits += 1;
};

const recordCacheMiss = () => {
    metricsState.cacheMisses += 1;
};

const recordProcessedJob = (processingTimeMs) => {
    metricsState.processedJobs += 1;
    metricsState.lastProcessingTimeMs = processingTimeMs;
};

const buildApiMetrics = (processingTimeMs, cacheStatus) => ({
    processingTime: formatProcessingTime(processingTimeMs),
    cache: cacheStatus
});

const getMetricsSnapshot = () => ({ ...metricsState });

module.exports = {
    startProcessingTimer,
    endProcessingTimer,
    formatProcessingTime,
    recordCacheHit,
    recordCacheMiss,
    recordProcessedJob,
    buildApiMetrics,
    getMetricsSnapshot
};
