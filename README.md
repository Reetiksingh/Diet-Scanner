# Nutrition Intelligence Backend System

## Problem Statement
People managing lifestyle diseases often need fast, reliable guidance from packaged food labels. Barcode lookup helps when products exist in the database, but many real labels still require manual reading. This system extends the existing Smart Diet Scanner backend with OCR-based label scanning, structured nutrition parsing, risk decisioning, caching, and async job processing.

## Features
- Existing barcode-based food scanning and recommendation flow
- New OCR nutrition label scanning (`POST /api/scan-label`)
- Parsing and normalization for nutrition text (calories, sugar, fat, protein, sodium)
- Rule-based decision engine (diabetes, hypertension, general)
- Redis-backed caching (with safe in-memory fallback)
- BullMQ async job pipeline (with safe in-memory fallback)
- Scan result polling endpoint (`GET /api/scan-result/:jobId`)
- Processing metrics in API responses (`processingTime`, `cache`)

## Architecture (Text Diagram)
```text
[Client / Frontend]
    |
    | POST /api/scan-label (multipart image)
    v
[Label Scan Controller]
    |
    |-- check cache (image hash / raw text hash)
    |      |-- HIT -> store completed job result -> return jobId
    |      |-- MISS -> enqueue job
    v
[Queue Layer: BullMQ or In-Memory Fallback]
    |
    v
[Worker]
    |
    |-- OCR (Tesseract.js)
    |-- parse + normalize nutrition
    |-- decision engine (healthRules.json)
    |-- cache structured result (TTL: 1 hour)
    |-- save job result
    v
[GET /api/scan-result/:jobId]
    |
    v
[Client receives status + nutrition + decision + metrics]
```

## Tech Stack
- Node.js
- Express
- MySQL (existing data layer)
- Redis / ioredis (cache and queue backend when configured)
- BullMQ (async job queue)
- Tesseract.js (OCR)
- Multer (multipart image upload)
- JWT auth (existing)

## API Endpoints

### Existing Endpoints (unchanged)
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/food/search/:barcode`
- `POST /api/recommendation/check`
- `GET /api/admin/stats`
- `POST /api/admin/food`
- Additional existing admin/food routes remain available

### New Nutrition Intelligence Endpoints
- `POST /api/scan-label`
  - Content-Type: `multipart/form-data`
  - Field: `image` (required)
  - Optional fields: `disease`, `condition`, `sync=true`
  - Optional field for OCR-only response: `rawOnly=true` (used with `sync=true`)
  - Default response (async):
    ```json
    {
      "jobId": "scan-...",
      "status": "QUEUED",
      "cache": "MISS",
      "queueMode": "bullmq"
    }
    ```
  - Optional sync response (`sync=true`):
    ```json
    {
      "rawText": "...",
      "nutrition": {
        "calories": 120,
        "sugar": 5,
        "fat": 2,
        "protein": 3,
        "sodium": 180
      },
      "decision": {
        "status": "MODERATE",
        "reasons": ["High sodium"]
      },
      "processingTime": "248ms",
      "cache": "MISS"
    }
    ```
  - Optional OCR-only response (`sync=true&rawOnly=true`):
    ```json
    {
      "rawText": "..."
    }
    ```

- `GET /api/scan-result/:jobId`
  - Response:
    ```json
    {
      "jobId": "scan-...",
      "status": "COMPLETED",
      "rawText": "...",
      "nutrition": {
        "calories": 120,
        "sugar": 5,
        "fat": 2,
        "protein": 3,
        "sodium": 180
      },
      "decision": {
        "status": "SAFE",
        "reasons": []
      },
      "processingTime": "248ms",
      "cache": "MISS"
    }
    ```

## How To Run

### 1. Backend setup
```bash
cd backend
npm install
```

### 2. Configure environment
Create/update `backend/.env` with existing DB/JWT values and optional Redis values:
```env
PORT=5000
DB_HOST=localhost
DB_USER=root
DB_PASSWORD=your_password
DB_NAME=diet_scanner
JWT_SECRET=your_secret_key

# Optional Redis/BullMQ
REDIS_HOST=127.0.0.1
REDIS_PORT=6379
REDIS_PASSWORD=
REDIS_DB=0
# Optional full URL alternative:
# REDIS_URL=redis://127.0.0.1:6379
```

If Redis is not configured, the new label scan pipeline still works using in-memory cache and in-memory async queue fallback.

### 3. Start backend
```bash
npm run dev
# or
npm start
```

### 4. Frontend
Open `frontend/index.html` in browser (or Live Server). Existing barcode flow remains unchanged, and a new OCR upload card is available in the scanner section.

## New Backend Module Structure
```text
backend/src/
  config/
    healthRules.json
    redisClient.js
  modules/
    decisionEngine/
      decision.service.js
    labelScan/
      scanLabel.controller.js
      scanLabel.routes.js
      scanLabel.service.js
      labelScan.queue.js
      labelScan.worker.js
  utils/
    hash.js
    logger.js
    metrics.js
    nutritionParser.js
```
