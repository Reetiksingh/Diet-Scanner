const formatMeta = (meta) => {
    if (!meta || (typeof meta === 'object' && Object.keys(meta).length === 0)) {
        return '';
    }

    try {
        return ` ${JSON.stringify(meta)}`;
    } catch (error) {
        return ' [unserializable-meta]';
    }
};

const log = (level, message, meta = {}) => {
    const existingLogger = global.logger;

    if (existingLogger && typeof existingLogger[level] === 'function') {
        existingLogger[level](message, meta);
        return;
    }

    const timestamp = new Date().toISOString();
    const entry = `[${timestamp}] [${level.toUpperCase()}] ${message}${formatMeta(meta)}`;

    if (level === 'error') {
        console.error(entry);
        return;
    }

    if (level === 'warn') {
        console.warn(entry);
        return;
    }

    console.log(entry);
};

module.exports = {
    info: (message, meta) => log('info', message, meta),
    warn: (message, meta) => log('warn', message, meta),
    error: (message, meta) => log('error', message, meta),
    debug: (message, meta) => log('debug', message, meta)
};
