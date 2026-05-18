const express = require('express');
const router = express.Router();
const pool = require('../db'); // Путь к твоему файлу db.js


// --- УПРАВЛЕНИЕ ПОЛЬЗОВАТЕЛЯМИ ---

// Получить всех пользователей
router.get('/users', async (req, res) => {
    try {
        const result = await pool.query('SELECT * FROM users ORDER BY user_id ASC');
        res.json(result.rows);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Добавить пользователя
// Добавить пользователя + привязать к группе
router.post('/users', async (req, res) => {
    const { full_name, email, password_hash, role_id, group_id } = req.body;

    try {
        const result = await pool.query(
            `INSERT INTO users (full_name, email, password_hash, role_id)
             VALUES ($1, $2, $3, $4)
             RETURNING *`,
            [full_name, email, password_hash, role_id]
        );

        const newUser = result.rows[0];

        if (role_id == 1 && group_id) {
            await pool.query(
                `INSERT INTO group_students (group_id, student_id)
                 VALUES ($1, $2)`,
                [group_id, newUser.user_id]
            );
        }

        res.json(newUser);
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: err.message });
    }
});

// Удалить пользователя
router.delete('/users/:id', async (req, res) => {
    try {
        await pool.query('DELETE FROM users WHERE user_id = $1', [req.params.id]);
        res.json({ message: 'Пользователь удален' });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// --- УПРАВЛЕНИЕ ПАРАМИ (LESSONS) ---

function localToUTC(dateStr, offsetHours = 0) {
    if (!dateStr) return null;

    // dateStr приходит как "2026-05-08T14:30"
    const date = new Date(dateStr);

    // Вычитаем локальный offset
    date.setHours(date.getHours() - offsetHours);

    return date.toISOString();
}

// Получить все пары с именами препода и группы
router.get('/lessons', async (req, res) => {
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

// Добавить пару
router.post('/lessons', async (req, res) => {

    const {
        teacher_id,
        group_id,
        subject,
        start_time,
        end_time,
        room,
        lesson_type,
        timezone_offset_hours
    } = req.body;

    if (!teacher_id || !group_id || !subject || !start_time || !end_time) {
        return res.status(400).json({
            error: 'Пожалуйста, заполните все обязательные поля'
        });
    }

    const offset = timezone_offset_hours || 0;

    try {

        const result = await pool.query(
            `INSERT INTO lessons
            (
                teacher_id,
                group_id,
                subject,
                start_time,
                end_time,
                room,
                lesson_type
            )
            VALUES ($1, $2, $3, $4, $5, $6, $7)
            RETURNING *`,
            [
                teacher_id,
                group_id,
                subject,
                localToUTC(start_time, offset),
                localToUTC(end_time, offset),
                room,
                lesson_type || 'normal'
            ]
        );

        res.json(result.rows[0]);

    } catch (err) {

        console.error(err);

        res.status(500).json({
            error: err.message
        });
    }
});

// Удалить пару
router.delete('/lessons/:id', async (req, res) => {
    try {
        await pool.query('DELETE FROM lessons WHERE lesson_id = $1', [req.params.id]);
        res.json({ message: 'Пара удалена' });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// --- ВСПОМОГАТЕЛЬНЫЕ ДАННЫЕ (Для выпадающих списков в Go) ---

router.get('/teachers', async (req, res) => {
    try {
        const result = await pool.query('SELECT user_id, full_name FROM users WHERE role_id = 2');
        res.json(result.rows);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

router.get('/groups', async (req, res) => {
    try {
        const result = await pool.query('SELECT * FROM groups');
        res.json(result.rows);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Создать группу
router.post('/groups', async (req, res) => {
  const { group_name, starosta_id } = req.body;

  if (!group_name || !group_name.trim()) {
    return res.status(400).json({ error: 'group_name обязателен' });
  }

  try {
    const result = await pool.query(
      `INSERT INTO groups (group_name, starosta_id)
       VALUES ($1, $2)
       RETURNING *`,
      [group_name.trim(), starosta_id ?? null]
    );

    res.json(result.rows[0]);
  } catch (err) {
    // уникальность group_name
    if (err.code === '23505') {
      return res.status(409).json({ error: 'Такая группа уже существует' });
    }
    console.error(err);
    res.status(500).json({ error: err.message });
  }
});



// Получить текущую пару преподавателя
router.get('/teacher/current-lesson/:id', async (req, res) => {
    const teacherId = req.params.id;
    const now = new Date().toISOString();

    try {
        const result = await pool.query(`
            SELECT l.lesson_id, l.subject, l.start_time, l.end_time,
                   g.group_id, g.group_name
            FROM lessons l
            JOIN groups g ON l.group_id = g.group_id
            WHERE l.teacher_id = $1
              AND l.start_time <= $2
              AND l.end_time >= $2
            LIMIT 1
        `, [teacherId, now]);

        if (result.rows.length === 0) {
            return res.json(null); // Нет активной пары
        }

        res.json(result.rows[0]);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});


// Текущая пара студента
router.get('/student/current-lesson/:id', async (req, res) => {

    const studentId = req.params.id;

    try {

        const result = await pool.query(`
            SELECT
                l.lesson_id,
                l.subject,
                l.start_time,
                l.end_time,
                l.group_id,
                g.group_name
            FROM lessons l
            JOIN group_students gs ON l.group_id = gs.group_id
            JOIN groups g ON l.group_id = g.group_id
            WHERE gs.student_id = $1
              AND l.start_time <= NOW()
              AND l.end_time >= NOW()
            LIMIT 1
        `, [studentId]);

        if (result.rows.length === 0) {
            return res.json(null);
        }

        res.json(result.rows[0]);

    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Ошибка сервера' });
    }
});


module.exports = router;