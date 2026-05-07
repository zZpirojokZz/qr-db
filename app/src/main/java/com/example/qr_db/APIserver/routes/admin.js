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
router.post('/users', async (req, res) => {
    const { full_name, email, password_hash, role_id } = req.body;
    try {
        const result = await pool.query(
            'INSERT INTO users (full_name, email, password_hash, role_id) VALUES ($1, $2, $3, $4) RETURNING *',
            [full_name, email, password_hash, role_id]
        );
        res.json(result.rows[0]);
    } catch (err) {
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
    const { teacher_id, group_id, subject, start_time, end_time, room } = req.body;

    // Проверка на пустые значения
    if (!teacher_id || !group_id || !subject || !start_time || !end_time) {
        return res.status(400).json({
            error: 'Пожалуйста, заполните все обязательные поля (преподаватель, группа, предмет, время начала и окончания)'
        });
    }

    try {
        const result = await pool.query(
            `INSERT INTO lessons (teacher_id, group_id, subject, start_time, end_time, room)
             VALUES ($1, $2, $3, $4, $5, $6) RETURNING *`,
            [teacher_id, group_id, subject, start_time, end_time, room]
        );
        res.json(result.rows[0]);
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: err.message });
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

module.exports = router;