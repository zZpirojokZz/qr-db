const express = require('express');
const router = express.Router();
const pool = require('../db');

// Получение данных профиля по ID
router.get('/:id', async (req, res) => {
    try {
        const { id } = req.params;
        const result = await pool.query(
            'SELECT user_id, full_name, email, role_id FROM users WHERE user_id = $1',
            [id]
        );

        if (result.rows.length === 0) {
            return res.status(404).json({ error: "Пользователь не найден" });
        }

        res.json(result.rows[0]);
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: "Ошибка сервера" });
    }
});

module.exports = router;