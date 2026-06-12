const express = require('express');
const router = express.Router();
const pool = require('../db');
const authMiddleware = require('../middleware/authMiddleware');

// Хелпер перевода локального времени в UTC (необходим для работы с расписанием)
function localToUTC(dateStr, offsetHours = 0) {
    if (!dateStr) return null;
    const date = new Date(dateStr);
    date.setHours(date.getHours() - offsetHours);
    return date.toISOString();
}

// --- УПРАВЛЕНИЕ УЧЕБНЫМ ПРОЦЕССОМ (Доступ: Администрация - роль 4) ---

// Получить все занятия учебного процесса
router.get('/lessons', authMiddleware([4]), async (req, res) => {
    try {
        const result = await pool.query(`
            SELECT l.*, u.full_name as teacher_name, g.group_name
            FROM lessons l
            JOIN users u ON l.teacher_id = u.user_id
            JOIN groups g ON l.group_id = g.group_id
            ORDER BY l.start_time ASC
        `);
        res.json(result.rows);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Добавить занятие (пара)
router.post('/lessons', authMiddleware([4]), async (req, res) => {
    const {
        teacher_id,
        group_id,
        subject,
        start_time,
        end_time,
        room,
        timezone_offset_hours
    } = req.body;

    if (!teacher_id || !group_id || !subject || !start_time || !end_time) {
        return res.status(400).json({ error: 'Пожалуйста, заполните все обязательные поля' });
    }

    const offset = timezone_offset_hours || 0;

    try {
        const result = await pool.query(
            `INSERT INTO lessons (teacher_id, group_id, subject, start_time, end_time, room)
             VALUES ($1, $2, $3, $4, $5, $6) RETURNING *`,
            [
                teacher_id,
                group_id,
                subject,
                localToUTC(start_time, offset),
                localToUTC(end_time, offset),
                room
            ]
        );
        res.json(result.rows[0]);
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: err.message });
    }
});

// Удалить занятие
router.delete('/lessons/:id', authMiddleware([4]), async (req, res) => {
    try {
        await pool.query('DELETE FROM lessons WHERE lesson_id = $1', [req.params.id]);
        res.json({ message: 'Пара удалена' });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Создать учебную группу
router.post('/groups', authMiddleware([4]), async (req, res) => {
    try {
        const { group_name } = req.body;
        const group = await pool.query(
            `INSERT INTO groups (group_name) VALUES ($1) RETURNING *`,
            [group_name]
        );
        res.json(group.rows[0]);
    } catch (err) {
        console.error(err);
        res.status(500).send('Ошибка создания группы');
    }
});

// Получить списки групп для выпадающих списков
router.get('/groups', authMiddleware([4]), async (req, res) => {
    try {
        const result = await pool.query('SELECT * FROM groups');
        res.json(result.rows);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Получить список преподавателей для назначения на пары
router.get('/teachers', authMiddleware([4]), async (req, res) => {
    try {
        const result = await pool.query('SELECT user_id, full_name FROM users WHERE role_id = 2');
        res.json(result.rows);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Получить полную ведомость оценок колледжа
router.get('/grades', authMiddleware([4]), async (req, res) => {
    try {
        const grades = await pool.query(`
            SELECT g.grade_id, g.lesson_id, u.full_name, g.grade, g.attendance, g.created_at
            FROM grades g
            JOIN users u ON g.student_id = u.user_id
            ORDER BY g.created_at DESC
        `);
        res.json(grades.rows);
    } catch (err) {
        console.error(err);
        res.status(500).send('Ошибка получения оценок');
    }
});

// Получить текущую пару конкретного преподавателя (доступно и учителям, и администрации)
router.get('/teacher/current-lesson/:id', authMiddleware([2, 4]), async (req, res) => {
    const teacherId = req.params.id;
    const now = new Date().toISOString();
    try {
        const result = await pool.query(`
            SELECT l.lesson_id, l.subject, l.start_time, l.end_time, g.group_id, g.group_name
            FROM lessons l
            JOIN groups g ON l.group_id = g.group_id
            WHERE l.teacher_id = $1 AND l.start_time <= $2 AND l.end_time >= $2
            LIMIT 1
        `, [teacherId, now]);

        if (result.rows.length === 0) {
            return res.json(null);
        }
        res.json(result.rows[0]);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});


// --- СТАТИСТИКА КОЛЛЕДЖА ---

// Средний балл
router.get('/college-average', authMiddleware([4]), async (req, res) => {
    try {
        const result = await pool.query(`
            SELECT ROUND(AVG(grade), 2) AS college_average FROM grades WHERE grade IS NOT NULL
        `);
        res.json(result.rows[0]);
    } catch (err) {
        console.error(err);
        res.status(500).send('Ошибка статистики');
    }
});

// Лучший студент
router.get('/best-student', authMiddleware([4]), async (req, res) => {
    try {
        const result = await pool.query(`
            SELECT u.full_name, ROUND(AVG(g.grade), 2) AS average_grade
            FROM users u
            JOIN grades g ON u.user_id = g.student_id
            WHERE g.grade IS NOT NULL
            GROUP BY u.user_id
            ORDER BY average_grade DESC LIMIT 1
        `);
        res.json(result.rows[0]);
    } catch (err) {
        console.error(err);
        res.status(500).send('Ошибка statistics');
    }
});

// Худший студент
router.get('/worst-student', authMiddleware([4]), async (req, res) => {
    try {
        const result = await pool.query(`
            SELECT u.full_name, ROUND(AVG(g.grade), 2) AS average_grade
            FROM users u
            JOIN grades g ON u.user_id = g.student_id
            WHERE g.grade IS NOT NULL
            GROUP BY u.user_id
            ORDER BY average_grade ASC LIMIT 1
        `);
        res.json(result.rows[0]);
    } catch (err) {
        console.error(err);
        res.status(500).send('Ошибка статистики');
    }
});

module.exports = router;