const express = require('express');
const router = express.Router();
const pool = require('../db');
const authMiddleware = require('../middleware/authMiddleware');


// Средний балл студента
router.get('/student/:id', authMiddleware([3,4]), async (req, res) => {

    try {

        const result = await pool.query(
            `
            SELECT
                u.user_id,
                u.full_name,
                ROUND(AVG(g.grade),2) AS average_grade
            FROM users u
            JOIN grades g
                ON u.user_id = g.student_id
            WHERE u.user_id = $1
            GROUP BY u.user_id, u.full_name
            `,
            [req.params.id]
        );

        res.json(result.rows);

    } catch(err) {
        console.error(err);
        res.status(500).send('Ошибка статистики');
    }

});

// Средний балл группы
router.get('/group/:id', authMiddleware([3,4]), async (req, res) => {

    try {

        const result = await pool.query(
            `
            SELECT
                g.group_id,
                g.group_name,
                ROUND(AVG(gr.grade),2) AS average_grade
            FROM groups g
            JOIN users u
                ON u.group_id = g.group_id
            JOIN grades gr
                ON gr.student_id = u.user_id
            WHERE g.group_id = $1
            GROUP BY g.group_id, g.group_name
            `,
            [req.params.id]
        );

        res.json(result.rows);

    } catch(err) {
        console.error(err);
        res.status(500).send('Ошибка статистики группы');
    }

});

// Средний балл колледжа
router.get('/college', authMiddleware([3,4]), async (req, res) => {

    try {

        const result = await pool.query(
            `
            SELECT
                ROUND(AVG(grade),2) AS college_average
            FROM grades
            `
        );

        res.json(result.rows[0]);

    } catch(err) {
        console.error(err);
        res.status(500).send('Ошибка статистики колледжа');
    }

});

// Лучший студент
router.get('/top-student', authMiddleware([3,4]), async (req, res) => {

    try {

        const result = await pool.query(
            `
            SELECT
                u.user_id,
                u.full_name,
                ROUND(AVG(g.grade),2) AS average_grade
            FROM users u
            JOIN grades g
                ON u.user_id = g.student_id
            GROUP BY u.user_id, u.full_name
            ORDER BY AVG(g.grade) DESC
            LIMIT 1
            `
        );

        res.json(result.rows[0]);

    } catch(err) {
        console.error(err);
        res.status(500).send('Ошибка статистики');
    }

});

// Худший студент
router.get('/worst-student', authMiddleware([3,4]), async (req, res) => {

    try {

        const result = await pool.query(
            `
            SELECT
                u.user_id,
                u.full_name,
                ROUND(AVG(g.grade),2) AS average_grade
            FROM users u
            JOIN grades g
                ON u.user_id = g.student_id
            GROUP BY u.user_id, u.full_name
            ORDER BY AVG(g.grade) ASC
            LIMIT 1
            `
        );

        res.json(result.rows[0]);

    } catch(err) {
        console.error(err);
        res.status(500).send('Ошибка статистики');
    }

});

module.exports = router;