const express = require('express');
const router = express.Router();
const pool = require('../db');

// ЛОГИН (ВРЕМЕННО БЕЗ ШИФРОВАНИЯ ДЛЯ ТЕСТА)
router.post('/login', async (req, res) => {
    const { email, password, password_hash } = req.body;
    const finalPassword = password || password_hash;

    console.log("--- ТЕСТОВЫЙ ВХОД ---");
    console.log("Ввод почты:", email);
    console.log("Ввод пароля:", finalPassword);

    try {
        const result = await pool.query('SELECT * FROM users WHERE email = $1', [email]);

        if (result.rows.length === 0) {
            console.log("Ошибка: Пользователь не найден");
            return res.status(400).send('Пользователь не найден');
        }

        const user = result.rows[0];
        console.log("База данных вернула:", user.password_hash);

        // ПРЯМОЕ СРАВНЕНИЕ ТЕКСТА (БЕЗ BCRYPT)
        if (finalPassword.trim() === user.password_hash.trim()) {
            console.log("УСПЕХ: Пароли совпали!");
            return res.json({
                user_id: user.user_id,
                full_name: user.full_name,
                email: user.email,
                role_id: user.role_id
            });
        } else {
            console.log("ОШИБКА: Пароли РАЗНЫЕ");
            return res.status(400).send('Неверный пароль');
        }

    } catch (err) {
        console.error("Ошибка:", err);
        res.status(500).send('Ошибка сервера');
    }
});

module.exports = router;
