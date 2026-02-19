#!/bin/bash

# Smart Diet Scanner - Complete Startup Script
# Run this from project root: bash start.sh

echo "🍎 Smart Diet Scanner - Starting System..."
echo ""

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo -e "${YELLOW}❌ Node.js is not installed. Please install Node.js first.${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Node.js found${NC}"

# Check if MySQL is running (optional check)
echo ""
echo -e "${BLUE}📊 Database Setup${NC}"
echo "Make sure MySQL is running and you've imported database/schema.sql"
echo ""

# Install backend dependencies
echo -e "${BLUE}📦 Installing Backend Dependencies...${NC}"
cd backend
npm install

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Dependencies installed${NC}"
else
    echo -e "${YELLOW}❌ Failed to install dependencies${NC}"
    exit 1
fi

# Check .env file
if [ ! -f ".env" ]; then
    echo -e "${YELLOW}⚠️  .env file not found. Creating default...${NC}"
    cat > .env << EOF
DB_HOST=localhost
DB_USER=root
DB_PASSWORD=root
DB_NAME=diet_scanner
JWT_SECRET=your_secret_key_here_change_in_production
PORT=5000
NODE_ENV=development
EOF
fi

echo ""
echo -e "${BLUE}🚀 Starting Backend Server...${NC}"
echo "Server will run on http://localhost:5000"
echo ""

# Start the server
npm start &
SERVER_PID=$!

echo -e "${GREEN}✅ Backend started (PID: $SERVER_PID)${NC}"
echo ""

echo -e "${BLUE}📱 Frontend Information${NC}"
echo "Open frontend/index.html in your browser"
echo "Or use VS Code Live Server: right-click → Open with Live Server"
echo ""

echo -e "${YELLOW}Test Credentials:${NC}"
echo "Email: user@test.com"
echo "Password: test@123"
echo "Disease: Diabetes"
echo ""

echo -e "${YELLOW}Sample Barcodes:${NC}"
echo "  • 8901002020020 - Regular Biscuits (Avoid)"
echo "  • 8901234567890 - Whole Wheat Bread (Safe)"
echo "  • 8901444555666 - Regular Cola (Avoid)"
echo "  • 8901777888999 - Oats Cereal (Safe)"
echo ""

echo -e "${GREEN}🎉 System Ready!${NC}"
echo "Press Ctrl+C to stop the server"
echo ""

# Keep script running
wait $SERVER_PID
