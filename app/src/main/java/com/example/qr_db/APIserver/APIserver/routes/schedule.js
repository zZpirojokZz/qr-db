const express = require('express');
const router = express.Router();
const pool = require('../db');

router.get('/today', async (req, res) => {
    try {
        const result = await pool.query(`
            SELECT
                g.group_name AS "groupName",
                l.room AS "room",
                l.start_time AS "start_time",
                l.end_time AS "end_time"
            FROM lessons l
            JOIN groups g ON l.group_id = g.group_id
            ORDER BY l.start_time
        `);

        res.json(result.rows);

    } catch (err) {
        console.error('Ошибка при получении расписания:', err);
        res.status(500).json({ error: 'Внутренняя ошибка сервера' });
    }
});

module.exports = router;