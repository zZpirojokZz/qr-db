const express = require('express');
const router = express.Router();
const pool = require('../db'); 
const jwt = require('jsonwebtoken');

// --- ВМЕСТЕ СРЕДСТВО ЗАЩИТЫ (ТОКЕН) ---
const authenticateToken = (req, res, next) => {
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1]; // Формат: Bearer <TOKEN>

    if (!token) return res.status(401).json({ error: 'Нет токена' });

    jwt.verify(token, 'smartcheck_secret', (err, user) => {
        if (err) return res.status(403).json({ error: 'Неверный токен' });
        req.user = user; // Сохраняем ID пользователя в запросе
        next();
    });
};

// ПОЛУЧЕНИЕ ПРОФИЛЯ (ЗАЩИЩЕННО)
router.get('/me', authenticateToken, async (req, res) => {
    try {
        const userId = req.user.user_id; // Берем ID из проверенного токена

        const result = await pool.query(
            "SELECT user_id, full_name, email, role_id FROM users WHERE user_id = $1",
            [userId]
        );

        if (result.rows.length === 0) {
            return res.status(404).json({ error: 'Пользователь не найден' });
        }

        res.json(result.rows[0]); // Возвращаем только нужные поля

    } catch (error) {
        console.error(error);
        res.status(500).json({ error: 'Ошибка сервера' });
    }
});

// ОПЦИОНАЛЬНО: Обновление профиля
router.put('/profile', authenticateToken, async (req, res) => {
    try {
        const { full_name, email } = req.body;
        const userId = req.user.user_id;

        const result = await pool.query(
            `UPDATE users SET full_name = $1, email = $2 WHERE user_id = $3 RETURNING user_id, full_name, email`,
            [full_name, email, userId]
        );

        res.json(result.rows[0]);
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Ошибка обновления' });
    }
});

module.exports = router;