const express = require('express');
const router = express.Router();
const pool = require('../db');
const authMiddleware = require('../middleware/authMiddleware');

router.post('/submit', authMiddleware([2]), async (req, res) => {
    
    const { lesson_id, grades } = req.body;

    try {
        const values = [];
        const placeholders = [];

        grades.forEach((g, index) => {
            const baseIndex = index * 4;

            placeholders.push(
                `($${baseIndex + 1}, $${baseIndex + 2}, $${baseIndex + 3}, $${baseIndex + 4})`
            );

            values.push(
                lesson_id,
                g.student_id,
                g.grade,
                g.attendance
            );
        });

        const query = `
            INSERT INTO grades (lesson_id, student_id, grade, attendance)
            VALUES ${placeholders.join(', ')}
        `;

        await pool.query(query, values);

        res.send('Оценки сохранены (bulk insert)');

    } catch (err) {
        console.error(err.message);
        res.status(500).send(err.message);
    }
});

module.exports = router;


// Отметить посещаемость через QR
router.post('/mark', async (req, res) => {

    const { lesson_id, student_id, attendance = true } = req.body;

    try {

        const existing = await pool.query(
            'SELECT 1 FROM grades WHERE lesson_id = $1 AND student_id = $2',
            [lesson_id, student_id]
        );

        if (existing.rows.length > 0) {
            return res.status(409).json({ error: 'Студент уже отмечен' });
        }

        await pool.query(
            `INSERT INTO grades (lesson_id, student_id, attendance)
             VALUES ($1, $2, $3)`,
            [lesson_id, student_id, attendance]
        );

        return res.status(200).json({ message: 'Студент отмечен' });

    } catch (err) {

        console.error("Ошибка mark:", err);   // ✅ тут err объявлен правильно
        return res.status(500).json({ error: 'Ошибка сервера' });

    }
});


// Проверить, отмечен ли студент
router.get('/status', async (req, res) => {

    const { lesson_id, student_id } = req.query;

    try {

        const result = await pool.query(
            'SELECT 1 FROM grades WHERE lesson_id = $1 AND student_id = $2',
            [lesson_id, student_id]
        );

        res.json({ marked: result.rows.length > 0 });

    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Ошибка сервера' });
    }
});