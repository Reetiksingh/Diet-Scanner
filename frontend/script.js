// API Configuration
const API_URL = 'http://localhost:8000/api';
let authToken = localStorage.getItem('authToken');
let currentUser = JSON.parse(localStorage.getItem('currentUser')) || null;
let scanHistory = JSON.parse(localStorage.getItem('scanHistory')) || [];

// ============ AUTH FUNCTIONS ============

function switchAuthTab(tab) {
    document.querySelectorAll('.auth-form').forEach(f => f.classList.remove('active'));
    document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));

    if (tab === 'login') {
        document.getElementById('loginForm').classList.add('active');
        document.querySelectorAll('.tab-btn')[0].classList.add('active');
    } else {
        document.getElementById('registerForm').classList.add('active');
        document.querySelectorAll('.tab-btn')[1].classList.add('active');
    }
}

// document.getElementById('loginForm').addEventListener('submit', async (e) => {
//     e.preventDefault();
//     const email = document.getElementById('loginEmail').value;
//     const password = document.getElementById('loginPassword').value;

//     try {
//         const response = await fetch(`${API_URL}/auth/login`, {
//             method: 'POST',
//             headers: { 'Content-Type': 'application/json' },
//             body: JSON.stringify({ email, password })
//         });

//         const data = await response.json();
//         console.log("LOGIN DATA:", data);   // 👈 ADD HERE

//         if (response.ok) {
//             authToken = data.token || data.data?.token; //added later
//             currentUser = data.user || data.data?.user; //added later
//             console.log("TOKEN:", authToken);       // 👈 ADD HERE
//             console.log("USER:", currentUser);     // 👈 ADD HERE

//             localStorage.setItem('authToken', authToken);
//             localStorage.setItem('currentUser', JSON.stringify(currentUser));

//             showDashboard();
//         } else {
//             alert(data.error || 'Login failed');
//         }
//     } catch (error) {
//         console.error('Login error:', error);
//         alert('Connection error. Check if backend is running on port 5000');
//     }
// });

// document.getElementById('registerForm').addEventListener('submit', async (e) => {
//     e.preventDefault();
//     const username = document.getElementById('regUsername').value;
//     const email = document.getElementById('regEmail').value;
//     const password = document.getElementById('regPassword').value;
//     const disease = document.getElementById('regDisease').value;

//     try {
//         const response = await fetch(`${API_URL}/auth/register`, {
//             method: 'POST',
//             headers: { 'Content-Type': 'application/json' },
//             body: JSON.stringify({ username, email, password, disease })
//         });

//         const data = await response.json();

//         if (response.ok) {
//             alert('Registration successful! Please login.');
//             switchAuthTab('login');
//         } else {
//             alert(data.error || 'Registration failed');
//         }
//     } catch (error) {
//         console.error('Register error:', error);
//         alert('Connection error');
//     }
// });

function logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('currentUser');
    authToken = null;
    currentUser = null;
    document.getElementById('loginPage').classList.add('active');
    document.getElementById('dashboardPage').classList.remove('active');
}

// ============ ADMIN MENU VISIBILITY ============

function updateAdminMenuVisibility() {
    const adminMenuItem = document.querySelector('.menu-item[onclick="switchPage(\'admin\')"]');
    const adminSection = document.getElementById('adminSection');
    
    if (!currentUser) {
        // Hide admin menu if no user
        if (adminMenuItem) adminMenuItem.style.display = 'none';
        return;
    }

    // Check if user is admin
    const isAdmin = currentUser.role === 'admin';
    
    if (adminMenuItem) {
        adminMenuItem.style.display = isAdmin ? 'block' : 'none';
    }
    
    // If user is not admin and somehow on admin page, redirect to scanner
    if (!isAdmin && adminSection && adminSection.classList.contains('active')) {
        switchPage('scanner');
    }
}

// ============ PAGE NAVIGATION ============

function showDashboard() {
    document.getElementById('loginPage').classList.remove('active');
    document.getElementById('dashboardPage').classList.add('active');

    // Update user info
    if (currentUser) {
        document.getElementById('userInfo').textContent = `Welcome, ${currentUser.username}!`;
        document.getElementById('profileDisease').textContent = getDiseaseDisplay(currentUser.disease);
        loadDietTips();
        
        // Hide/show admin menu based on role
        updateAdminMenuVisibility();
    }
}

function switchPage(page) {
    document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
    document.querySelectorAll('.menu-item').forEach(m => m.classList.remove('active'));

    switch (page) {
        case 'scanner':
            document.getElementById('scannerSection').classList.add('active');
            document.querySelectorAll('.menu-item')[0].classList.add('active');
            break;
        case 'history':
            document.getElementById('historySection').classList.add('active');
            document.querySelectorAll('.menu-item')[1].classList.add('active');
            loadScanHistory();
            break;
        case 'tips':
            document.getElementById('tipsSection').classList.add('active');
            document.querySelectorAll('.menu-item')[2].classList.add('active');
            break;
        case 'admin':
            document.getElementById('adminSection').classList.add('active');
            document.querySelectorAll('.menu-item')[3].classList.add('active');
            loadAdminStats();
            break;
    }
}

// ============ SCANNER FUNCTIONS ============

let cameraActive = false;
let qrScanInterval = null;
let canvas = null;
let canvasContext = null;

async function toggleCamera() {
    const video = document.getElementById('video');
    const cameraBtn = document.getElementById('cameraBtn');

    if (!cameraActive) {
        try {
            const stream = await navigator.mediaDevices.getUserMedia({
                video: { facingMode: 'environment' }
            });

            video.srcObject = stream;
            await video.play();
            cameraActive = true;
            cameraBtn.textContent = '📷 Stop Camera';

            // Setup canvas for QR scanning
            if (!canvas) {
                canvas = document.createElement('canvas');
                canvasContext = canvas.getContext('2d');
            }

            // Start QR code scanning
            startQRScanning(video);

        } catch (error) {
            alert('Camera access denied: ' + error.message);
        }
    } else {
        // Stop QR scanning
        stopQRScanning();

        if (video.srcObject) {
            video.srcObject.getTracks().forEach(track => track.stop());
            video.srcObject = null;
        }

        cameraActive = false;
        cameraBtn.textContent = '📷 Start Camera';
    }
}

function startQRScanning(video) {
    if (qrScanInterval) {
        clearInterval(qrScanInterval);
    }

    qrScanInterval = setInterval(() => {
        if (video.readyState === video.HAVE_ENOUGH_DATA) {
            canvas.width = video.videoWidth;
            canvas.height = video.videoHeight;
            canvasContext.drawImage(video, 0, 0, canvas.width, canvas.height);

            const imageData = canvasContext.getImageData(0, 0, canvas.width, canvas.height);
            
            // Use jsQR library to decode QR code
            if (typeof jsQR !== 'undefined') {
                const code = jsQR(imageData.data, imageData.width, imageData.height);
                
                if (code) {
                    // QR code detected!
                    const barcode_id = code.data;
                    console.log('QR Code detected:', barcode_id);
                    
                    // Stop scanning
                    stopQRScanning();
                    
                    // Search food by barcode_id
                    searchFoodByBarcodeId(barcode_id);
                }
            }
        }
    }, 100); // Check every 100ms
}

function stopQRScanning() {
    if (qrScanInterval) {
        clearInterval(qrScanInterval);
        qrScanInterval = null;
    }
}

async function searchFoodByBarcodeId(barcode_id) {
    try {
        // Search food by barcode_id (QR code contains barcode_id)
        const response = await fetch(`${API_URL}/food/search/${barcode_id}`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        const data = await response.json();
        
        if (data.success && data.food) {
            // Found food, get recommendation
            getRecommendation(data.food);
        } else {
            alert('Food not found for QR code: ' + barcode_id + '\n\nPlease ensure the food is added to the database by an admin.');
        }
    } catch (error) {
        console.error('Error searching food:', error);
        alert('Error searching food. Please try again.');
    }
}

function searchFood() {
    const barcode = document.getElementById('barcodeInput').value.trim();

    if (!barcode) {
        alert('Please enter or scan a barcode');
        return;
    }

    // Search in local database
    const sampleFoods = [
        { id: 1, name: 'Regular Biscuits', barcode: '8901002020020', calories: 480, sugar: 12, carbs: 65, protein: 6, fat: 20, fiber: 2, sodium: 200, alternatives: [{ name: 'Oats Biscuits', rating: 4.5 }, { name: 'Roasted Chana', rating: 4.7 }] },
        { id: 2, name: 'Whole Wheat Bread', barcode: '8901234567890', calories: 265, sugar: 3, carbs: 49, protein: 9, fat: 3, fiber: 7, sodium: 400, alternatives: [{ name: 'Ragi Bread', rating: 4.6 }] },
        { id: 3, name: 'Regular Cola', barcode: '8901444555666', calories: 140, sugar: 39, carbs: 39, protein: 0, fat: 0, fiber: 0, sodium: 40, alternatives: [{ name: 'Herbal Tea', rating: 4.7 }] },
        { id: 4, name: 'Oats Cereal', barcode: '8901777888999', calories: 150, sugar: 1, carbs: 27, protein: 5, fat: 3, fiber: 4, sodium: 100, alternatives: [{ name: 'Ragi Porridge', rating: 4.6 }] }
    ];

    const food = sampleFoods.find(f => f.barcode === barcode);

    if (food) {
        getRecommendation(food);
    } else {
        alert('Food not found in database. Try another barcode.');
    }
}

async function getRecommendation(food) {
    try {
        const response = await fetch(`${API_URL}/recommendation/check`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify({ food, disease: currentUser.disease })
        });

        const result = await response.json();
        displayResult(result);
        addToHistory(food, result);
    } catch (error) {
        console.error('Error:', error);
        // Offline mode - use local rule engine
        const localResult = applyLocalRules(food, currentUser.disease);
        displayResult(localResult);
        addToHistory(food, localResult);
    }
}

function applyLocalRules(food, disease) {
    let verdict = '✅ Safe';
    let score = 100;
    let violations = [];

    // Simple rule engine
    if (food.sugar > 5) {
        score -= 30;
        violations.push(`High sugar content: ${food.sugar}g`);
    }
    if (food.calories > 300) {
        score -= 20;
        violations.push(`Moderate calories: ${food.calories} kcal`);
    }
    if (food.sodium > 200) {
        score -= 15;
        violations.push(`High sodium: ${food.sodium}mg`);
    }

    if (violations.length > 2 || score < 30) {
        verdict = '❌ Avoid';
    } else if (violations.length > 0) {
        verdict = '⚠ Limit';
    }

    return {
        food_name: food.name,
        verdict,
        score,
        disease_name: getDiseaseDisplay(disease),
        violations,
        warnings: [],
        nutrition: {
            calories: `${food.calories} kcal`,
            sugar: `${food.sugar}g`,
            carbs: `${food.carbs}g`,
            protein: `${food.protein}g`,
            fat: `${food.fat}g`,
            fiber: `${food.fiber}g`,
            sodium: `${food.sodium}mg`
        },
        alternatives: food.alternatives || [],
        recommendation: getRecommendationText(verdict, disease)
    };
}

function displayResult(result) {
    const container = document.getElementById('resultContainer');
    const verdictClass = result.verdict.includes('Safe') ? 'verdict-safe' :
        result.verdict.includes('Limit') ? 'verdict-limit' : 'verdict-avoid';

    let html = `
    <div class="result-box">
      <div class="result-header">
        <h3>${result.food_name}</h3>
        <span class="verdict-badge ${verdictClass}">${result.verdict}</span>
      </div>
      
      <div class="nutrition-grid">
        <div class="nutrition-item">
          <label>Calories</label>
          <value>${result.nutrition.calories}</value>
        </div>
        <div class="nutrition-item">
          <label>Sugar</label>
          <value>${result.nutrition.sugar}</value>
        </div>
        <div class="nutrition-item">
          <label>Carbs</label>
          <value>${result.nutrition.carbs}</value>
        </div>
        <div class="nutrition-item">
          <label>Protein</label>
          <value>${result.nutrition.protein}</value>
        </div>
        <div class="nutrition-item">
          <label>Fat</label>
          <value>${result.nutrition.fat}</value>
        </div>
        <div class="nutrition-item">
          <label>Fiber</label>
          <value>${result.nutrition.fiber}</value>
        </div>
      </div>

      <div class="violations-warnings">
  `;

    if (result.violations && result.violations.length > 0) {
        result.violations.forEach(v => {
            html += `<div class="violation-item">❌ ${v}</div>`;
        });
    }

    if (result.warnings && result.warnings.length > 0) {
        result.warnings.forEach(w => {
            html += `<div class="warning-item">⚠️ ${w}</div>`;
        });
    }

    html += `
      </div>

      <div class="recommendation">
        <strong>💡 Recommendation:</strong> ${result.recommendation}
      </div>
  `;

    if (result.alternatives && result.alternatives.length > 0) {
        html += `
      <div class="alternatives-section">
        <h4>✨ Healthy Alternatives:</h4>
    `;
        result.alternatives.forEach(alt => {
            html += `
        <div class="alternative-item">
          <strong>${alt.name}</strong> ⭐ ${alt.rating}
        </div>
      `;
        });
        html += `</div>`;
    }

    html += `</div>`;

    document.getElementById('resultContent').innerHTML = html;
    container.style.display = 'block';
}

function getRecommendationText(verdict, disease) {
    const recommendations = {
        diabetes: {
            '✅ Safe': 'This food is safe for your diabetes management. Includes low sugar and healthy carbs.',
            '⚠ Limit': 'This food can be consumed occasionally. Monitor portion sizes and blood sugar levels.',
            '❌ Avoid': 'High in sugar/refined carbs. Choose whole grains and low-GI alternatives instead.'
        },
        pcod: {
            '✅ Safe': 'Excellent for PCOD! High in protein and nutrients needed for hormonal balance.',
            '⚠ Limit': 'Contains refined carbs. Pair with protein and fiber for better insulin response.',
            '❌ Avoid': 'Can trigger inflammation and worsen PCOD symptoms. Choose anti-inflammatory foods.'
        },
        thyroid: {
            '✅ Safe': 'Supports thyroid health with essential nutrients.',
            '⚠ Limit': 'Consume moderately to avoid thyroid function interference.',
            '❌ Avoid': 'Contains compounds that interfere with thyroid function. Avoid.'
        },
        obesity: {
            '✅ Safe': 'Low-calorie, high-fiber option. Perfect for healthy weight management.',
            '⚠ Limit': 'Moderate calories. Use portion control and balance with other meals.',
            '❌ Avoid': 'Too calorie-dense for weight management. Choose lower-calorie alternatives.'
        },
        bp: {
            '✅ Safe': 'Low sodium, heart-healthy choice. Great for BP management.',
            '⚠ Limit': 'Moderate sodium content. Consume occasionally.',
            '❌ Avoid': 'High in sodium/saturated fat. Increases BP. Choose heart-healthy alternatives.'
        }
    };

    return (recommendations[disease.toLowerCase()] || recommendations.diabetes)[verdict];
}

// ============ HISTORY FUNCTIONS ============

function addToHistory(food, result) {
    const historyItem = {
        foodName: food.name,
        verdict: result.verdict,
        date: new Date().toLocaleString(),
        score: result.score
    };

    scanHistory.unshift(historyItem);
    if (scanHistory.length > 50) scanHistory.pop();
    localStorage.setItem('scanHistory', JSON.stringify(scanHistory));
}

function loadScanHistory() {
    const historyList = document.getElementById('historyList');

    if (scanHistory.length === 0) {
        historyList.innerHTML = '<p>No scans yet. Start scanning food items!</p>';
        return;
    }

    let html = '';
    scanHistory.forEach((item, index) => {
        const verdictClass = item.verdict.includes('Safe') ? 'verdict-safe' :
            item.verdict.includes('Limit') ? 'verdict-limit' : 'verdict-avoid';
        html += `
      <div class="history-item">
        <div class="history-item-info">
          <strong>${item.foodName}</strong>
          <div class="history-item-date">${item.date}</div>
        </div>
        <span class="history-item-verdict ${verdictClass}">${item.verdict}</span>
      </div>
    `;
    });

    historyList.innerHTML = html;
}

// ============ DIET TIPS FUNCTIONS ============

function loadDietTips() {
    const tips = getDiseaseSpecificTips(currentUser.disease);
    const tipsContent = document.getElementById('tipsContent');

    let html = '';
    tips.forEach(tip => {
        html += `
      <div class="tip-card">
        <h4>${tip.title}</h4>
        <p>${tip.description}</p>
      </div>
    `;
    });

    tipsContent.innerHTML = html;
}

function getDiseaseSpecificTips(disease) {
    const tips = {
        diabetes: [
            { title: '🌾 Choose Whole Grains', description: 'Whole wheat, oats, and brown rice have lower glycemic index and help manage blood sugar.' },
            { title: '🥗 Eat More Fiber', description: 'Vegetables, legumes, and fruits slow sugar absorption and keep you fuller longer.' },
            { title: '💧 Stay Hydrated', description: 'Drink water instead of sugary drinks. Aim for 8-10 glasses daily.' },
            { title: '🕐 Portion Control', description: 'Eat smaller portions more frequently to maintain stable blood sugar levels.' },
            { title: '🚫 Avoid Sugar', description: 'Skip refined sugar, candies, pastries, and sugary beverages completely.' }
        ],
        pcod: [
            { title: '🥚 Increase Protein Intake', description: 'Include eggs, fish, chicken, and legumes to balance hormones.' },
            { title: '🥑 Healthy Fats', description: 'Consume omega-3 rich foods like salmon, walnuts, and flax seeds.' },
            { title: '🌶️ Anti-inflammatory Foods', description: 'Turmeric, ginger, and leafy greens reduce inflammation.' },
            { title: '⚡ Regular Exercise', description: 'Combine cardio and strength training for 30-45 minutes daily.' },
            { title: '😴 Adequate Sleep', description: 'Sleep 7-8 hours to regulate hormones and reduce insulin resistance.' }
        ],
        thyroid: [
            { title: '🍤 Boost Iodine Intake', description: 'Include iodized salt, seafood, and dairy products in your diet.' },
            { title: '🌰 Selenium Foods', description: 'Brazil nuts, eggs, and chicken support thyroid function.' },
            { title: '🚫 Avoid Goitrogens', description: 'Limit raw cruciferous vegetables like cabbage and broccoli.' },
            { title: '⏰ Regular Checkups', description: 'Monitor TSH levels and thyroid function regularly.' },
            { title: '💊 Medicine Timing', description: 'Take thyroid medication on empty stomach, 30 minutes before food.' }
        ],
        obesity: [
            { title: '📉 Calorie Deficit', description: 'Consume 300-500 calories less than your daily requirement.' },
            { title: '🥗 High Fiber Diet', description: 'Vegetables, whole grains, and fruits keep you satiated.' },
            { title: '💪 Exercise Daily', description: 'Aim for 150 minutes of moderate exercise per week.' },
            { title: '🚫 Avoid Processed Foods', description: 'Skip fried foods, sugary items, and ultra-processed snacks.' },
            { title: '💧 Drink Water', description: 'Drink water before meals to reduce appetite and calorie intake.' }
        ],
        bp: [
            { title: '🧂 Low Sodium Diet', description: 'Limit salt intake to less than 2,300mg per day.' },
            { title: '🥗 DASH Diet', description: 'Focus on vegetables, fruits, whole grains, and lean proteins.' },
            { title: '💪 Regular Exercise', description: '30 minutes moderate activity 5 days a week helps lower BP.' },
            { title: '😌 Stress Management', description: 'Practice meditation, yoga, or deep breathing exercises.' },
            { title: '⚠️ Limit Alcohol', description: 'Reduce alcohol consumption to lower blood pressure.' }
        ]
    };

    return tips[disease] || tips.diabetes;
}

// ============ ADMIN FUNCTIONS ============

function switchAdminTab(tab) {
    document.querySelectorAll('.admin-tab-btn').forEach(b => b.classList.remove('active'));
    document.querySelectorAll('.admin-tab-content').forEach(c => c.classList.remove('active'));

    if (tab === 'dashboard') {
        document.querySelectorAll('.admin-tab-btn')[0].classList.add('active');
        document.getElementById('adminDashboard').classList.add('active');
        loadAdminStats();
    } else if (tab === 'addFood') {
        document.querySelectorAll('.admin-tab-btn')[1].classList.add('active');
        document.getElementById('addFoodTab').classList.add('active');
    } else if (tab === 'foods') {
        document.querySelectorAll('.admin-tab-btn')[2].classList.add('active');
        document.getElementById('foodsTab').classList.add('active');
        loadAllFoods();
    } else {
        document.querySelectorAll('.admin-tab-btn')[3].classList.add('active');
        document.getElementById('historyTab').classList.add('active');
        loadAdminHistory();
    }
}

async function loadAdminStats() {
    try {
        const response = await fetch(`${API_URL}/admin/stats`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        const data = await response.json();
        
        if (data.success) {
            document.getElementById('statUsers').textContent = data.total_users.toLocaleString();
            document.getElementById('statFoods').textContent = data.total_foods.toLocaleString();
            document.getElementById('statScans').textContent = data.daily_scans.toLocaleString();
        }
    } catch (error) {
        console.error('Error loading stats:', error);
    }
}

async function loadAllFoods() {
    try {
        const response = await fetch(`${API_URL}/admin/foods`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        const data = await response.json();
        
        if (data.success) {
            const foodsList = document.getElementById('foodsList');
            if (!foodsList) return;

            if (data.foods.length === 0) {
                foodsList.innerHTML = '<p>No foods in database. Add your first food!</p>';
                return;
            }

            let html = '<div class="foods-grid">';
            data.foods.forEach(food => {
                html += `
                    <div class="food-card">
                        <h4>${food.name}</h4>
                        <p><strong>Barcode:</strong> ${food.barcode || 'N/A'}</p>
                        <p><strong>Category:</strong> ${food.category || 'N/A'}</p>
                        ${food.barcode_id ? `
                            <div class="qr-preview-container">
                                <img src="${API_URL}/admin/food/${food.id}/qr" alt="QR Code" class="qr-preview">
                            </div>
                            <button onclick="downloadQR(${food.id}, '${food.name}')" class="btn btn-small">Download QR</button>
                        ` : ''}
                        <div class="food-actions">
                            <button onclick="editFood(${food.id})" class="btn btn-small">Edit</button>
                            <button onclick="deleteFood(${food.id})" class="btn btn-small btn-danger">Delete</button>
                        </div>
                    </div>
                `;
            });
            html += '</div>';
            foodsList.innerHTML = html;
        }
    } catch (error) {
        console.error('Error loading foods:', error);
        if (error.message.includes('403')) {
            alert('Access denied. Admin privileges required.');
        }
    }
}

async function loadAdminHistory() {
    try {
        const response = await fetch(`${API_URL}/admin/history`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        const data = await response.json();
        
        if (data.success) {
            const historyList = document.getElementById('historyList');
            if (!historyList) return;

            if (data.history.length === 0) {
                historyList.innerHTML = '<p>No scan history yet.</p>';
                return;
            }

            let html = '<div class="history-table">';
            data.history.forEach(item => {
                const verdictClass = item.verdict && item.verdict.includes('Safe') ? 'verdict-safe' :
                    item.verdict && item.verdict.includes('Limit') ? 'verdict-limit' : 'verdict-avoid';
                
                html += `
                    <div class="history-row">
                        <div class="history-cell"><strong>${item.food_name || 'Unknown'}</strong></div>
                        <div class="history-cell">${item.username || 'Unknown'}</div>
                        <div class="history-cell">${item.disease || 'N/A'}</div>
                        <div class="history-cell"><span class="verdict-badge ${verdictClass}">${item.verdict || 'N/A'}</span></div>
                        <div class="history-cell">${new Date(item.created_at).toLocaleString()}</div>
                    </div>
                `;
            });
            html += '</div>';
            historyList.innerHTML = html;
        }
    } catch (error) {
        console.error('Error loading history:', error);
    }
}

function downloadQR(foodId, foodName) {
    const link = document.createElement('a');
    link.href = `${API_URL}/admin/food/${foodId}/qr`;
    link.download = `QR-${foodName.replace(/\s+/g, '-')}.png`;
    link.click();
}

function downloadGeneratedQR() {
    if (window.lastGeneratedQR) {
        const link = document.createElement('a');
        link.href = window.lastGeneratedQR.qrDataURL;
        link.download = `QR-${window.lastGeneratedQR.foodName.replace(/\s+/g, '-')}.png`;
        link.click();
    }
}

async function editFood(foodId) {
    // TODO: Implement edit food modal/form
    alert('Edit food functionality - Coming soon!');
}

async function deleteFood(foodId) {
    if (!confirm('Are you sure you want to delete this food?')) {
        return;
    }

    try {
        const response = await fetch(`${API_URL}/admin/food/${foodId}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        const data = await response.json();
        
        if (data.success) {
            alert('Food deleted successfully');
            loadAllFoods();
        } else {
            alert('Error deleting food: ' + (data.error || 'Unknown error'));
        }
    } catch (error) {
        console.error('Error deleting food:', error);
        alert('Error deleting food');
    }
}

// ============ UTILITY FUNCTIONS ============

function getDiseaseDisplay(disease) {
    const displays = {
        diabetes: '🩺 Diabetes Management',
        pcod: '🏥 PCOD Management',
        thyroid: '⚕️ Thyroid Management',
        obesity: '💪 Obesity Management',
        bp: '❤️ BP Management'
    };
    return displays[disease] || disease;
}

// ============ INITIALIZATION ============

window.addEventListener('load', () => {
    if (authToken && currentUser) {
        currentUser = JSON.parse(localStorage.getItem('currentUser'));
        updateAdminMenuVisibility();
        showDashboard();
    } else {
        document.getElementById('loginPage').classList.add('active');
    }
});


document.addEventListener("DOMContentLoaded", () => {

    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const email = document.getElementById('loginEmail').value;
        const password = document.getElementById('loginPassword').value;

        try {
            const response = await fetch(`${API_URL}/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password })
            });

            const data = await response.json();
            console.log("LOGIN DATA:", data);

            if (response.ok) {
                authToken = data.token;
                currentUser = data.user;

                console.log("TOKEN:", authToken);
                console.log("USER:", currentUser);

                localStorage.setItem('authToken', authToken);
                localStorage.setItem('currentUser', JSON.stringify(currentUser));

                // Hide/show admin menu based on role
                updateAdminMenuVisibility();

                showDashboard();
            } else {
                alert(data.error || 'Login failed');
            }
        } catch (error) {
            console.error('Login error:', error);
            alert('Connection error');
        }
    });

    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const username = document.getElementById('regUsername').value;
        const email = document.getElementById('regEmail').value;
        const password = document.getElementById('regPassword').value;
        const disease = document.getElementById('regDisease').value;

        try {
            const response = await fetch(`${API_URL}/auth/register`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, email, password, disease })
            });

            const data = await response.json();

            if (response.ok) {
                alert('Registration successful! Please login.');
                switchAuthTab('login');
            } else {
                alert(data.error || 'Registration failed');
            }
        } catch (error) {
            console.error('Register error:', error);
            alert('Connection error');
        }
    });

    // Admin form submission
    const addFoodForm = document.getElementById('addFoodForm');
    if (addFoodForm) {
        addFoodForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const foodData = {
                name: document.getElementById('foodName').value,
                barcode: document.getElementById('foodBarcode').value,
                description: document.getElementById('foodDescription').value,
                category: document.getElementById('foodCategory').value,
                brand: document.getElementById('foodBrand').value,
                calories: parseFloat(document.getElementById('foodCalories').value) || null,
                sugar: parseFloat(document.getElementById('foodSugar').value) || null,
                carbs: parseFloat(document.getElementById('foodCarbs').value) || null,
                protein: parseFloat(document.getElementById('foodProtein').value) || null,
                fat: parseFloat(document.getElementById('foodFat').value) || null,
                saturated_fat: parseFloat(document.getElementById('foodSaturatedFat').value) || null,
                fiber: parseFloat(document.getElementById('foodFiber').value) || null,
                sodium: parseFloat(document.getElementById('foodSodium').value) || null
            };

            const btnText = document.getElementById('addFoodBtnText');
            const spinner = document.getElementById('addFoodSpinner');
            const message = document.getElementById('addFoodMessage');
            
            btnText.style.display = 'none';
            spinner.style.display = 'inline';
            message.style.display = 'none';

            try {
                const response = await fetch(`${API_URL}/admin/food`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${authToken}`
                    },
                    body: JSON.stringify(foodData)
                });

                const data = await response.json();

                if (response.ok && data.success) {
                    message.textContent = 'Food added successfully!';
                    message.className = 'message success';
                    message.style.display = 'block';
                    
                    // Show QR code
                    if (data.food && data.food.qr_code) {
                        const qrContainer = document.getElementById('qrResultContainer');
                        const qrPreview = document.getElementById('qrPreview');
                        qrPreview.innerHTML = `<img src="${data.food.qr_code}" alt="QR Code" style="max-width: 300px;">`;
                        qrContainer.style.display = 'block';
                        
                        // Store QR data for download
                        window.lastGeneratedQR = {
                            foodId: data.food.id,
                            foodName: data.food.name,
                            qrDataURL: data.food.qr_code
                        };
                    }
                    
                    // Reset form
                    addFoodForm.reset();
                    
                    // Reload foods list if on that tab
                    if (document.getElementById('foodsTab').classList.contains('active')) {
                        loadAllFoods();
                    }
                } else {
                    message.textContent = data.error || 'Failed to add food';
                    message.className = 'message error';
                    message.style.display = 'block';
                }
            } catch (error) {
                console.error('Error adding food:', error);
                message.textContent = 'Connection error. Please try again.';
                message.className = 'message error';
                message.style.display = 'block';
            } finally {
                btnText.style.display = 'inline';
                spinner.style.display = 'none';
            }
        });
    }

});
