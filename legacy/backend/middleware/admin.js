const { authenticateToken } = require('./auth');

/**
 * Admin Middleware
 * Verifies JWT token and checks if user has admin role
 * Must be used after authenticateToken middleware
 */
const adminMiddleware = (req, res, next) => {
    // authenticateToken should have already set req.user
    if (!req.user) {
        return res.status(401).json({ error: 'Authentication required' });
    }

    // Check if user has admin role
    if (req.user.role !== 'admin') {
        return res.status(403).json({ error: 'Access denied. Admin privileges required.' });
    }

    next();
};

module.exports = { adminMiddleware };
