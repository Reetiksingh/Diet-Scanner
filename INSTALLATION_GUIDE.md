# 🚀 Installation & Deployment Guide

## Table of Contents
1. [System Requirements](#system-requirements)
2. [Local Development Setup](#local-development-setup)
3. [Database Setup](#database-setup)
4. [Running the Application](#running-the-application)
5. [Troubleshooting](#troubleshooting)
6. [Production Deployment](#production-deployment)

---

## System Requirements

### Minimum Requirements
- **OS**: Windows / macOS / Linux
- **Node.js**: v14.0 or higher
- **MySQL**: v5.7 or higher
- **RAM**: 2GB
- **Disk Space**: 500MB
- **Browser**: Chrome, Firefox, Safari, Edge (latest)

### Recommended
- **Node.js**: v18+ (LTS)
- **MySQL**: v8.0+
- **RAM**: 4GB+
- **SSD**: For faster database operations

---

## Local Development Setup

### Step 1: Clone/Download Project
```bash
# Navigate to your projects folder
cd /Users/retix/Desktop/workSpace/projectdi

# Or if cloning from git
git clone <repository-url>
cd projectdi
```

### Step 2: Verify Node.js Installation
```bash
# Check Node.js version
node --version
# Should show: v14.x.x or higher

# Check npm version
npm --version
# Should show: v6.x.x or higher
```

### Step 3: Check MySQL Installation
```bash
# macOS
brew services list | grep mysql

# Windows (in Command Prompt)
mysql --version

# Linux
mysql --version

# Start MySQL if not running
# macOS
brew services start mysql

# Windows (Admin Command Prompt)
net start MySQL80

# Linux
sudo service mysql start
```

---

## Database Setup

### Option 1: Using Command Line (Recommended)

```bash
# Navigate to database folder
cd database

# Import schema (macOS/Linux)
mysql -u root -p < schema.sql
# Enter password when prompted

# Import schema (Windows)
mysql -u root -p < schema.sql
# Or
mysql -u root -pYOUR_PASSWORD < schema.sql
```

### Option 2: Using MySQL Workbench (GUI)

1. Open MySQL Workbench
2. Click "Create a new SQL tab"
3. Open `database/schema.sql` file
4. Execute all queries (Cmd+Enter or Ctrl+Enter)
5. Verify database created: `SHOW DATABASES;`

### Option 3: Using phpMyAdmin

1. Open phpMyAdmin in browser (usually http://localhost/phpmyadmin)
2. Create new database named `diet_scanner`
3. Import `database/schema.sql` using "Import" tab

### Verify Database Setup
```sql
# Connect to MySQL
mysql -u root -p

# Check if database exists
SHOW DATABASES;

# Should see: diet_scanner

# Use the database
USE diet_scanner;

# Check tables
SHOW TABLES;

# Should show all 7 tables
SHOW TABLES;
```

---

## Running the Application

### Step 1: Install Backend Dependencies

```bash
# Navigate to backend folder
cd backend

# Install dependencies
npm install

# Verify installation
npm list
```

This installs:
- express (web framework)
- mysql2 (database driver)
- cors (enable cross-origin requests)
- body-parser (parse JSON)
- dotenv (environment variables)
- bcryptjs (password hashing)
- jsonwebtoken (JWT auth)

### Step 2: Configure Environment Variables

Edit `backend/.env`:
```env
DB_HOST=localhost
DB_USER=root
DB_PASSWORD=your_mysql_password
DB_NAME=diet_scanner
JWT_SECRET=your_very_secret_key_change_this
PORT=5000
NODE_ENV=development
```

**Important**: Change JWT_SECRET to something unique!

### Step 3: Start Backend Server

```bash
# From backend directory
npm start

# Output should show:
# Smart Diet Scanner Server running on port 5000
# Connected to database successfully
```

The server will:
- Connect to MySQL database
- Start listening on port 5000
- Be ready to accept API requests

### Step 4: Open Frontend

**Method 1: Direct File**
```bash
# Open in browser
open frontend/index.html

# Or drag file to browser
# Or use: file:///full/path/to/frontend/index.html
```

**Method 2: VS Code Live Server (Recommended)**
```bash
# Install extension: "Live Server" by Ritwick Dey
# Right-click frontend/index.html
# Select "Open with Live Server"
# Browser opens automatically on port 5500
```

**Method 3: Python SimpleHTTP**
```bash
# Navigate to frontend folder
cd frontend

# Python 3
python -m http.server 8000

# Python 2
python -m SimpleHTTPServer 8000

# Access on http://localhost:8000
```

**Method 4: Node SimpleHTTP**
```bash
# Install globally
npm install -g http-server

# Navigate to frontend
cd frontend

# Start server
http-server

# Access on http://localhost:8080
```

### Step 5: Test the Application

1. **Create Account**
   - Click "Register"
   - Enter: username, email, password, disease
   - Click "Register"

2. **Login**
   - Use email and password you registered
   - Should see dashboard

3. **Scan Food**
   - Click "Scan Food"
   - Try barcode: `8901002020020`
   - Should show recommendation

4. **Check History**
   - Click "History"
   - Should see previous scans

5. **View Tips**
   - Click "Diet Tips"
   - Should see disease-specific tips

---

## Troubleshooting

### Error: Cannot Find Module 'express'
```bash
# Solution
cd backend
npm install
# Try again
npm start
```

### Error: Connect ECONNREFUSED (MySQL)
```
Error: Connect ECONNREFUSED 127.0.0.1:3306
```

**Solutions**:
1. Check MySQL is running
```bash
# macOS
brew services start mysql

# Windows (Admin Command Prompt)
net start MySQL80

# Linux
sudo service mysql start
```

2. Check credentials in `.env`
```bash
# Test connection
mysql -h localhost -u root -p
```

3. Create database if not exists
```bash
mysql -u root -p < database/schema.sql
```

### Error: Port 5000 Already in Use
```bash
# Find what's using port 5000
# macOS/Linux
lsof -i :5000

# Windows
netstat -ano | findstr :5000

# Kill the process
# macOS/Linux
kill -9 <PID>

# Or use different port
# Edit backend/.env
PORT=5001
```

### Error: CORS Error in Frontend
```
Access to XMLHttpRequest blocked by CORS policy
```

**Solution**:
- Make sure backend is running on port 5000
- Check `API_URL` in `frontend/script.js` is correct
- Verify CORS is enabled in `backend/server.js`

### Error: Database Schema Error
```sql
-- If tables don't exist, reimport
mysql -u root -p < database/schema.sql

-- Or manually create database first
CREATE DATABASE IF NOT EXISTS diet_scanner;
USE diet_scanner;
-- Then import schema
```

### Error: JWT Token Expired
- Clear browser localStorage
- Logout and login again
- Or extend expiry in `backend/.env`

### Frontend Not Loading
- Check browser console (F12 → Console)
- Verify `frontend/index.html` exists
- Check `frontend/script.js` and `frontend/styles.css` paths
- Use Live Server instead of file:// protocol

---

## Production Deployment

### Before Deployment Checklist
- [ ] Change JWT_SECRET to unique value
- [ ] Change DB_PASSWORD to secure password
- [ ] Set NODE_ENV=production
- [ ] Enable HTTPS
- [ ] Setup database backups
- [ ] Configure firewall rules
- [ ] Setup monitoring and logging
- [ ] Load test the application

### Deploy to Heroku

```bash
# Install Heroku CLI
# From https://devcenter.heroku.com/articles/heroku-cli

# Login to Heroku
heroku login

# Create app
heroku create smart-diet-scanner

# Add MySQL (ClearDB)
heroku addons:create cleardb:ignite

# Set environment variables
heroku config:set JWT_SECRET=your_production_secret
heroku config:set NODE_ENV=production

# Deploy
git push heroku main

# Check logs
heroku logs --tail
```

### Deploy to AWS

```bash
# 1. Create EC2 instance (Ubuntu)
# 2. Connect to instance
# 3. Install Node.js and MySQL
# 4. Clone repository
# 5. Setup environment variables
# 6. Start application with PM2

npm install -g pm2
pm2 start backend/server.js
pm2 startup
pm2 save
```

### Deploy to DigitalOcean

```bash
# 1. Create Droplet (Ubuntu 20.04)
# 2. SSH into droplet
# 3. Install dependencies
curl -sL https://deb.nodesource.com/setup_16.x | sudo -E bash -
sudo apt-get install -y nodejs

# 4. Install MySQL
sudo apt-get install -y mysql-server

# 5. Clone and setup repository
# 6. Configure Nginx as reverse proxy
# 7. Setup SSL with Let's Encrypt
# 8. Start application with systemd
```

### Deploy to Docker

Create `Dockerfile`:
```dockerfile
FROM node:16

WORKDIR /app

COPY backend/package*.json ./

RUN npm install

COPY backend .

EXPOSE 5000

CMD ["npm", "start"]
```

Build and run:
```bash
docker build -t smart-diet-scanner .
docker run -p 5000:5000 smart-diet-scanner
```

---

## Monitoring & Maintenance

### Monitor Application Health
```bash
# Check if backend is running
curl http://localhost:5000/api/health

# Should return: {"status":"Server is running"}
```

### View Logs
```bash
# Backend logs (in terminal where npm start runs)
# Or redirect to file
npm start > server.log 2>&1 &

# Check log file
tail -f server.log
```

### Database Backup
```bash
# Export database
mysqldump -u root -p diet_scanner > backup.sql

# Restore from backup
mysql -u root -p diet_scanner < backup.sql
```

### Performance Optimization
1. Add database indexes on frequently queried columns
2. Implement caching with Redis
3. Use compression middleware
4. Optimize frontend assets
5. Setup CDN for static files

---

## Security Hardening

### For Production

1. **Use HTTPS**
   - Install SSL certificate
   - Redirect HTTP to HTTPS

2. **Environment Variables**
   ```bash
   # Use .env.production for production secrets
   DB_PASSWORD=secure_password
   JWT_SECRET=very_long_random_string
   ```

3. **Database Security**
   ```sql
   -- Create limited user
   CREATE USER 'app_user'@'localhost' IDENTIFIED BY 'password';
   GRANT SELECT, INSERT, UPDATE ON diet_scanner.* TO 'app_user'@'localhost';
   ```

4. **API Rate Limiting**
   ```javascript
   // Add to backend/server.js
   const rateLimit = require('express-rate-limit');
   const limiter = rateLimit({
     windowMs: 15 * 60 * 1000,
     max: 100
   });
   app.use(limiter);
   ```

5. **Enable CORS Properly**
   ```javascript
   app.use(cors({
     origin: ['https://yourdomain.com'],
     credentials: true
   }));
   ```

---

## Performance Tips

### Optimize Database
```sql
-- Add indexes for frequently queried fields
CREATE INDEX idx_barcode ON food(barcode);
CREATE INDEX idx_user_id ON scan_history(user_id);
CREATE INDEX idx_disease_rules ON disease_rules(disease_id);
```

### Optimize Node.js
```javascript
// Use clustering for multiple cores
const cluster = require('cluster');
const os = require('os');

if (cluster.isMaster) {
  for (let i = 0; i < os.cpus().length; i++) {
    cluster.fork();
  }
}
```

### Optimize Frontend
- Minify CSS and JS
- Optimize images
- Use lazy loading
- Cache resources

---

## Testing the Installation

Run this checklist:

```bash
# 1. Check Node.js
node --version

# 2. Check npm
npm --version

# 3. Check MySQL
mysql --version

# 4. Connect to MySQL
mysql -u root -p

# 5. List databases
SHOW DATABASES;
# Should see: diet_scanner

# 6. Exit MySQL
exit

# 7. Start backend
cd backend
npm install
npm start

# 8. In another terminal, test API
curl http://localhost:5000/api/health

# 9. Open frontend in browser
# Should see login page

# 10. Try to register and login
```

If all steps complete successfully, **installation is complete!** ✅

---

## Getting Help

### Common Issues Checklist
- [ ] MySQL is running
- [ ] Node.js version is 14+
- [ ] npm dependencies installed
- [ ] .env file configured
- [ ] Database schema imported
- [ ] Backend server started
- [ ] Frontend opened in browser
- [ ] No port conflicts

### Debug Mode
```bash
# Run backend with debug logs
DEBUG=* npm start

# Or manually add logs
console.log('DEBUG:', variable);
```

### Browser Console
- Press F12 to open Developer Tools
- Check Console tab for errors
- Check Network tab for API calls

---

## Success Indicators

✅ **Backend Working**
- Server shows "running on port 5000"
- `/api/health` returns status

✅ **Frontend Working**
- Login page displays
- Can create account
- Can login successfully

✅ **Database Working**
- Can query tables
- Sample data visible
- No connection errors

✅ **End-to-End Working**
- Scan a barcode
- Get recommendation
- See in history

---

**Installation Complete!** You're ready to use the system. 🎉

