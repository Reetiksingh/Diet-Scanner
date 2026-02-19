@echo off
REM Smart Diet Scanner - Complete Startup Script (Windows)
REM Run from project root: start.cmd

echo.
echo   ============================================
echo   🍎 Smart Diet Scanner - Starting System
echo   ============================================
echo.

REM Check if Node.js is installed
node --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Node.js is not installed. Please install Node.js first.
    pause
    exit /b 1
)

echo ✅ Node.js found: && node --version
echo.

echo 📊 Database Setup
echo Make sure MySQL is running and you've imported database/schema.sql
echo.

REM Navigate to backend
cd backend

echo 📦 Installing Backend Dependencies...
call npm install

if errorlevel 1 (
    echo ❌ Failed to install dependencies
    pause
    exit /b 1
)

echo ✅ Dependencies installed
echo.

REM Check .env file
if not exist ".env" (
    echo ⚠️  .env file not found. Creating default...
    (
        echo DB_HOST=localhost
        echo DB_USER=root
        echo DB_PASSWORD=root
        echo DB_NAME=diet_scanner
        echo JWT_SECRET=your_secret_key_here_change_in_production
        echo PORT=5000
        echo NODE_ENV=development
    ) > .env
)

echo.
echo 🚀 Starting Backend Server...
echo Server will run on http://localhost:5000
echo.

REM Start the server
start "Smart Diet Scanner Backend" cmd /k npm start

timeout /t 3 /nobreak

echo ✅ Backend started
echo.
echo 📱 Frontend Information
echo Open frontend/index.html in your browser
echo Or use VS Code Live Server extension
echo.

echo Test Credentials:
echo   Email: user@test.com
echo   Password: test@123
echo   Disease: Diabetes
echo.

echo Sample Barcodes:
echo   • 8901002020020 - Regular Biscuits (Avoid)
echo   • 8901234567890 - Whole Wheat Bread (Safe)
echo   • 8901444555666 - Regular Cola (Avoid)
echo   • 8901777888999 - Oats Cereal (Safe)
echo.

echo 🎉 System Ready!
echo Close this window to stop the server
echo.

pause
