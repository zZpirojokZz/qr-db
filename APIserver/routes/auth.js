const express = require('express');
const router = express.Router();
const pool = require('../db'); 
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');

// ЛОГИН
router.post('/login', async (req, res) => {
    try {
        const { email, password } = req.body;

        const user = await pool.query(
            `SELECT * FROM users WHERE email = $1`,
            [email]
        );

        if (user.rows.length === 0) {
            return res.status(404).json({ error: 'Пользователь не найден' });
        }

        const validPassword = await bcrypt.compare(password, user.rows[0].password_hash);

        if (!validPassword) {
            return res.status(401).json({ error: 'Неверный пароль' });
        }

        // Создаем токен
        const token = jwt.sign(
            { user_id: user.rows[0].user_id },
            'smartcheck_secret',
            { expiresIn: '12h' }
        );

        // Возвращает сообщение, токен и роль пользователя
        res.json({
            message: 'Успешный вход',
            token: token,
            role_id: user.rows[0].role_id
        });

    } catch (err) {
        console.error(err.message);
        res.status(500).json({ error: 'Ошибка входа' });
    }
});

// РЕГИСТРАЦИЯ
router.post('/register', async (req, res) => {
    try {
        const { full_name, email, password, role_id } = req.body;
        
        const hashedPassword = await bcrypt.hash(password, 10);

        const result = await pool.query(
            `INSERT INTO users (full_name, email, password_hash, role_id) VALUES ($1, $2, $3, $4) RETURNING user_id, full_name, email`,
            [full_name, email, hashedPassword, role_id || 1]
        );

        // Регистрируем тоже минимально или просто успех
        res.json({ message: 'Регистрация успешна', userId: result.rows[0].user_id });

    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Ошибка регистрации' });
    }
});

module.exports = router;