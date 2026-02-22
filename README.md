# Smart Diet Scanner - Complete Documentation

## 📋 Project Overview

Smart Diet Scanner is an intelligent nutrition guidance system designed for people with lifestyle diseases (Diabetes, PCOD, Thyroid, Obesity, BP). Users can scan food barcodes to get instant recommendations based on their health condition.

---

## 🏗️ Project Structure

```
projectdi/
├── backend/                 # Node.js Express Backend
│   ├── server.js           # Main server file
│   ├── db.js               # Database connection
│   ├── package.json        # Dependencies
│   ├── .env                # Environment variables
│   ├── middleware/
│   │   └── auth.js         # JWT authentication
│   ├── routes/
│   │   ├── auth.js         # Login/Register routes
│   │   ├── food.js         # Food search routes
│   │   ├── recommendation.js   # Recommendation routes
│   │   └── admin.js        # Admin routes
│   └── utils/
│       ├── diseaseRuleEngine.js    # Core recommendation logic
│       └── foodDatabase.js         # Sample food data
├── frontend/               # React-Free Frontend
│   ├── index.html         # Main HTML
│   ├── styles.css         # Styling
│   └── script.js          # Frontend JavaScript
├── database/
│   └── schema.sql         # Database schema
├── docs/
│   ├── README.md          # This file
│   ├── DFD.md             # Data Flow Diagrams
│   ├── FLOWCHART.md       # System Flowcharts
│   └── VIVA_QA.md         # Viva Questions & Answers
└── README.md
```

---

## 🚀 Installation & Setup

### Prerequisites
- Node.js (v14+)
- MySQL (v5.7+)
- Modern web browser
- Git

### Step 1: Clone/Download Project
```bash
cd /Users/retix/Desktop/workSpace/projectdi
```

### Step 2: Setup Backend

```bash
cd backend
npm install
```

Edit `.env` file with your database credentials:
```
DB_HOST=localhost
DB_USER=root
DB_PASSWORD=your_password
DB_NAME=diet_scanner
JWT_SECRET=your_secret_key
PORT=5000
```

### Step 3: Setup Database

Open MySQL and run:
```bash
mysql -u root -p < ../database/schema.sql
```

Or paste the content of `database/schema.sql` in MySQL Workbench.

### Step 4: Start Backend Server

```bash
npm start
# OR for development with auto-reload
npm run dev
```

Server will run on `http://localhost:5000`

### Step 5: Open Frontend

Open `frontend/index.html` in a modern web browser or use Live Server extension in VS Code.

---

## 🔐 User Roles

### 1. Normal User
- Register with username, email, password, and disease
- Login to dashboard
- Scan food barcodes
- View nutrition information
- Get disease-specific recommendations
- See scan history
- Get personalized diet tips

### 2. Admin User
- Add new foods to database
- Update nutrition information
- Manage disease rules
- View dashboard statistics
- Manage user database

---

## 📱 How to Use

### Registration
1. Click "Register" tab
2. Enter username, email, password
3. Select your condition (Diabetes, PCOD, Thyroid, Obesity, BP)
4. Click Register

### Login
1. Enter email and password
2. Click Login
3. Access personalized dashboard

### Scanning Food
1. Click "Scan Food" menu
2. Option 1: Enter barcode manually
3. Option 2: Click "📷 Start Camera" to scan with camera
4. Click "Search Food" button
5. Get verdict and recommendations

### Checking History
1. Click "History" menu
2. View all previous scans
3. See date and verdict for each food

### Diet Tips
1. Click "💡 Diet Tips" menu
2. View disease-specific recommendations
3. Get personalized health guidance

---

## 🎯 Recommendation Logic

### Disease Rules Engine

#### Diabetes Rules
- Sugar: ≤ 5g ✅
- Refined Carbs: ≤ 30g ✅
- Glycemic Index: ≤ 55 ✅
- Fiber: ≥ 5g ✅

#### PCOD Rules
- Protein: ≥ 25g ✅
- Refined Carbs: ≤ 35g ✅
- Sugar: ≤ 5g ✅
- Anti-inflammatory ✅

#### Thyroid Rules
- Iodine: 100-150 mcg ✅
- Selenium: Required ✅
- Soy: Avoid ❌
- Raw Cruciferous: Limit ⚠

#### Obesity Rules
- Calories: ≤ 300 kcal ✅
- Fat: ≤ 10g ✅
- Sugar: ≤ 3g ✅
- Fiber: ≥ 5g ✅

#### BP Rules
- Sodium: ≤ 200mg ✅
- Saturated Fat: ≤ 2g ✅
- Potassium: ≥ 150mg ✅

### Verdict System
- ✅ **SAFE**: Food meets all requirements
- ⚠️ **LIMIT**: Some concerns, consume occasionally
- ❌ **AVOID**: Does not meet disease requirements

---

## 📊 Database Schema

### Users Table
```sql
- id (PRIMARY KEY)
- username
- email (UNIQUE)
- password (hashed)
- disease (ENUM)
- created_at
- updated_at
```

### Food Table
```sql
- id (PRIMARY KEY)
- name
- barcode (UNIQUE)
- description
- category
- brand
```

### Nutrition Table
```sql
- id (PRIMARY KEY)
- food_id (FOREIGN KEY)
- calories
- sugar, carbs, protein
- fat, fiber, sodium
- potassium, iron, calcium
```

### Diseases Table
```sql
- id (PRIMARY KEY)
- disease_name (UNIQUE)
- description
- symptoms
```

### Disease_Rules Table
```sql
- id (PRIMARY KEY)
- disease_id (FOREIGN KEY)
- nutrient
- min_value, max_value
- action (avoid/limit/safe)
- priority
```

### Scan_History Table
```sql
- id (PRIMARY KEY)
- user_id (FOREIGN KEY)
- food_id (FOREIGN KEY)
- verdict
- score
- created_at
```

### Alternatives Table
```sql
- id (PRIMARY KEY)
- food_id (FOREIGN KEY)
- alternative_food_id (FOREIGN KEY)
- rating
```

---

## 🔌 API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login

### Food Management
- `GET /api/food/search/:barcode` - Search food by barcode
- `GET /api/food/:id` - Get food by ID
- `GET /api/food/` - Get all foods

### Recommendations
- `POST /api/recommendation/check` - Get food recommendation

### Admin
- `POST /api/admin/food/add` - Add new food
- `PUT /api/admin/disease-rules/:disease` - Update disease rules
- `GET /api/admin/stats` - Get dashboard statistics

---

## 🧪 Sample Barcodes for Testing

Try scanning these barcodes in the app:
- `8901002020020` - Regular Biscuits (High sugar)
- `8901234567890` - Whole Wheat Bread (Safe)
- `8901111222333` - Refined Flour (Limited)
- `8901444555666` - Regular Cola (Avoid)
- `8901777888999` - Oats Cereal (Safe)

---

## 🔒 Security Features

- Password hashing with bcryptjs
- JWT token-based authentication
- Token expiration (7 days)
- CORS enabled for frontend-backend communication
- Environment variables for sensitive data

---

## 📈 Advantages

✔ User-friendly interface  
✔ Real-world health problem solving  
✔ Disease-specific recommendations  
✔ Offline functionality available  
✔ Responsive design  
✔ Scalable architecture  

---

## ⚠️ Limitations

❌ Only packaged foods (with barcode)  
❌ Depends on database accuracy  
❌ No real-time AI image detection  
❌ Limited to predefined diseases  

---

## 🚀 Future Enhancements

🔮 AI food image recognition  
🔮 Mobile app version (React Native)  
🔮 Doctor consultation integration  
🔮 Voice assistant  
🔮 Meal planning module  
🔮 Wearable device integration  
🔮 Social community features  

---

## 📞 Support & Contact

For issues or suggestions, please check:
- Backend logs: `backend/` folder
- Frontend console: Developer tools (F12)
- Database connection: Check MySQL service


---

## ✨ Author Notes

This complete end-to-end system demonstrates:
- Full-stack web development
- Database design and management
- RESTful API architecture
- Frontend-backend integration
- Business logic implementation
- Health informatics application

Perfect for BCA/BTech projects and viva demonstrations!
