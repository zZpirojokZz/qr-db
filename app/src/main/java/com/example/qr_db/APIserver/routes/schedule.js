const express = require('express');
const router = express.Router();
const pool = require('../db'); // Убедись, что путь к db.js правильный

router.get('/today', async (req, res) => {
    try {
        // ВАЖНО: Мы берем данные из таблиц lessons и groups, а не schedule!
        const result = await pool.query(`
            SELECT 
                g.group_name AS group_name, 
                l.room AS room 
            FROM lessons l
            JOIN groups g ON l.group_id = g.group_id
        `);

        // Отправляем результат
        res.json(result.rows);
        
    } catch (err) {
        console.error('Ошибка при получении расписания:', err);
        res.status(500).json({ error: 'Внутренняя ошибка сервера' });
    }
});

module.exports = router;