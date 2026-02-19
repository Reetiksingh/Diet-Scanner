// Sample food database with alternatives

const foodDatabase = [
    {
        id: 1,
        name: 'Regular Biscuits',
        barcode: '8901002020020',
        calories: 480,
        sugar: 12,
        carbs: 65,
        protein: 6,
        fat: 20,
        fiber: 2,
        sodium: 200,
        type: 'processed',
        alternatives: [
            { name: 'Oats Biscuits', rating: 4.5 },
            { name: 'Roasted Chana Snacks', rating: 4.7 }
        ]
    },
    {
        id: 2,
        name: 'Whole Wheat Bread',
        barcode: '8901234567890',
        calories: 265,
        sugar: 3,
        carbs: 49,
        protein: 9,
        fat: 3,
        fiber: 7,
        sodium: 400,
        type: 'healthy',
        alternatives: [
            { name: 'Multi-grain Bread', rating: 4.6 },
            { name: 'Ragi Bread', rating: 4.5 }
        ]
    },
    {
        id: 3,
        name: 'Refined Flour',
        barcode: '8901111222333',
        calories: 364,
        sugar: 0,
        carbs: 76,
        protein: 10,
        fat: 1,
        fiber: 2,
        sodium: 2,
        type: 'processed',
        alternatives: [
            { name: 'Whole Wheat Flour', rating: 4.8 },
            { name: 'Millet Flour', rating: 4.6 }
        ]
    },
    {
        id: 4,
        name: 'Regular Cola',
        barcode: '8901444555666',
        calories: 140,
        sugar: 39,
        carbs: 39,
        protein: 0,
        fat: 0,
        fiber: 0,
        sodium: 40,
        type: 'unhealthy',
        alternatives: [
            { name: 'Herbal Tea', rating: 4.7 },
            { name: 'Lemon Water', rating: 4.9 }
        ]
    },
    {
        id: 5,
        name: 'Oats Cereal',
        barcode: '8901777888999',
        calories: 150,
        sugar: 1,
        carbs: 27,
        protein: 5,
        fat: 3,
        fiber: 4,
        sodium: 100,
        type: 'healthy',
        alternatives: [
            { name: 'Ragi Porridge', rating: 4.6 },
            { name: 'Millets Cereal', rating: 4.5 }
        ]
    }
];

module.exports = foodDatabase;
