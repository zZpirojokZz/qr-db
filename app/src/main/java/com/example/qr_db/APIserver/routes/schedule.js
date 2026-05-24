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

// Расписание студента на сегодня
router.get('/student/:id', async (req, res) => {

    const studentId = req.params.id;

    try {

        const result = await pool.query(`
            SELECT
                l.lesson_id,
                l.lesson_type,
                l.subject,
                l.room,
                l.start_time,
                l.end_time,
                u.full_name AS teacher_name,
                g.group_name
            FROM lessons l
            JOIN users u ON l.teacher_id = u.user_id
            JOIN groups g ON l.group_id = g.group_id
            JOIN group_students gs ON l.group_id = gs.group_id
            WHERE gs.student_id = $1
            ORDER BY l.start_time ASC
        `, [studentId]);

        res.json(result.rows);

    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Ошибка сервера' });
    }
});



// Уникальные предметы группы
router.get('/group/:groupId/subjects', async (req, res) => {

    const groupId = req.params.groupId;

    try {

        const result = await pool.query(`
            SELECT DISTINCT l.subject
            FROM lessons l
            WHERE l.group_id = $1
            ORDER BY l.subject ASC
        `, [groupId]);

        // Возвращаем просто массив строк
        res.json(result.rows.map(r => r.subject));

    } catch (err) {
        console.error('Ошибка при получении предметов группы:', err);
        res.status(500).json({ error: 'Ошибка сервера' });
    }
});




module.exports = router;