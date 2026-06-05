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
);
