const express = require('express');
const logger = require('../../utils/logger');
const { scanLabel, getScanResult } = require('./scanLabel.controller');

let multer = null;
try {
    multer = require('multer');
} catch (error) {
    logger.warn('multer not installed. /api/scan-label upload endpoint will return dependency error until installed.');
}

const router = express.Router();

if (multer) {
    const upload = multer({
        storage: multer.memoryStorage(),
        limits: {
            fileSize: Number(process.env.MAX_LABEL_IMAGE_SIZE || 5 * 1024 * 1024)
        },
        fileFilter: (req, file, callback) => {
            if (!file.mimetype || !file.mimetype.startsWith('image/')) {
                callback(new Error('Only image uploads are supported.'));
                return;
            }

            callback(null, true);
        }
    });

    router.post('/scan-label', (req, res, next) => {
        upload.single('image')(req, res, (error) => {
            if (error) {
                return res.status(400).json({ error: error.message });
            }

            return next();
        });
    }, scanLabel);
} else {
    router.post('/scan-label', (req, res) => {
        res.status(500).json({
            error: 'multer dependency is missing. Please run npm install in backend to enable file uploads.'
        });
    });
}

router.get('/scan-result/:jobId', getScanResult);

module.exports = router;
