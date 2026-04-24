const pool = require('../../../db');

let ensureTablePromise = null;

const ensureScanResultsTable = async () => {
    if (!ensureTablePromise) {
        ensureTablePromise = (async () => {
            const connection = await pool.getConnection();

            try {
                await connection.execute(`
                    CREATE TABLE IF NOT EXISTS scan_results (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        user_id INT NULL,
                        raw_text TEXT NULL,
                        parsed_data JSON NULL,
                        decision VARCHAR(50) NULL,
                        status VARCHAR(20) NOT NULL DEFAULT 'pending',
                        job_id VARCHAR(120) NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        UNIQUE KEY uniq_scan_results_job_id (job_id)
                    )
                `);
            } finally {
                connection.release();
            }
        })();
    }

    return ensureTablePromise;
};

const createPendingScanResult = async ({ jobId, userId = null }) => {
    await ensureScanResultsTable();

    const connection = await pool.getConnection();

    try {
        await connection.execute(
            'INSERT INTO scan_results (user_id, status, job_id) VALUES (?, ?, ?)',
            [userId, 'pending', jobId]
        );
    } finally {
        connection.release();
    }
};

const updateCompletedScanResult = async ({ jobId, rawText, parsedData, decision }) => {
    await ensureScanResultsTable();

    const connection = await pool.getConnection();

    try {
        await connection.execute(
            `UPDATE scan_results
             SET raw_text = ?, parsed_data = ?, decision = ?, status = ?
             WHERE job_id = ?`,
            [rawText, JSON.stringify(parsedData), decision, 'completed', jobId]
        );
    } finally {
        connection.release();
    }
};

const updateFailedScanResult = async ({ jobId, errorMessage }) => {
    await ensureScanResultsTable();

    const connection = await pool.getConnection();

    try {
        await connection.execute(
            `UPDATE scan_results
             SET parsed_data = ?, decision = ?, status = ?
             WHERE job_id = ?`,
            [JSON.stringify({ error: errorMessage }), 'FAILED', 'failed', jobId]
        );
    } finally {
        connection.release();
    }
};

const getScanResultByJobId = async (jobId) => {
    await ensureScanResultsTable();

    const connection = await pool.getConnection();

    try {
        const [rows] = await connection.execute(
            `SELECT id, user_id, raw_text, parsed_data, decision, status, job_id, created_at
             FROM scan_results
             WHERE job_id = ?
             LIMIT 1`,
            [jobId]
        );

        return rows[0] || null;
    } finally {
        connection.release();
    }
};

module.exports = {
    ensureScanResultsTable,
    createPendingScanResult,
    updateCompletedScanResult,
    updateFailedScanResult,
    getScanResultByJobId
};
