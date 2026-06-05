const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
require('dotenv').config();
console.log("PORT FROM ENV:", process.env.PORT);


const app = express();

// Middleware
app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// Import Routes
const authRoutes = require('./routes/auth');
const foodRoutes = require('./routes/food');
const recommendationRoutes = require('./routes/recommendation');
const adminRoutes = require('./routes/admin');
const scanLabelRoutes = require('./src/modules/labelScan/scanLabel.routes');
const { initializeLabelScanWorker } = require('./src/modules/labelScan/labelScan.worker');
const { ensureScanResultsTable } = require('./src/modules/labelScan/scanResult.repository');

// Use Routes
app.use('/api/auth', authRoutes);
app.use('/api/food', foodRoutes);
app.use('/api/recommendation', recommendationRoutes);
app.use('/api/admin', adminRoutes);
app.use('/api', scanLabelRoutes);

// Health Check
app.get('/api/health', (req, res) => {
    res.json({ status: 'Server is running' });
});

const PORT = process.env.PORT || 5000;

ensureScanResultsTable().catch((error) => {
    console.error('Failed to initialize scan_results table:', error.message);
});

initializeLabelScanWorker();

app.listen(PORT, () => {
    console.log(`Smart Diet Scanner Server running on port ${PORT}`);
});
