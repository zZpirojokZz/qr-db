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