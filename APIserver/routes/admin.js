const express = require('express');
const router = express.Router();
const pool = require('../db');
const authMiddleware = require('../middleware/authMiddleware');
const bcrypt = require('bcrypt');

// Разрешаем доступ и Суперадмину (3), и Администрации (4), чтобы панель работала под ролью 4
const allowedRoles = [3, 4];


// 1. УПРАВЛЕНИЕ ПОЛЬЗОВАТЕЛЯМИ

// Получить всех пользователей для таблицы в админке
router.get('/users', authMiddleware(allowedRoles), async (req, res) => {
    try {
        const result = await pool.query('SELECT user_id, full_name, email, role_id, group_id, created_at FROM users ORDER BY user_id ASC');
        res.json(result.rows);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Добавить пользователя через админку (с хэшированием пароля)
router.post('/users', authMiddleware(allowedRoles), async (req, res) => {
    const { full_name, email, password_hash, role_id } = req.body; // password_hash приходит чистым из инпута админки
    try {
        // Хэшируем пароль перед записью в БД, чтобы пользователь мог авторизоваться
        const hashedPassword = await bcrypt.hash(password_hash, 10);
        
        const result = await pool.query(
            'INSERT INTO users (full_name, email, password_hash, role_id) VALUES ($1, $2, $3, $4) RETURNING user_id, full_name, email, role_id',
            [full_name, email, hashedPassword, role_id]
        );
        res.json(result.rows[0]);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Удалить пользователя
router.delete('/users/:id', authMiddleware(allowedRoles), async (req, res) => {
    try {
        await pool.query('DELETE FROM users WHERE user_id = $1', [req.params.id]);
        res.json({ message: 'Пользователь удален' });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Твой роут массового добавления студентов (оставляем без изменений)
router.post('/users/bulk', authMiddleware([3]), async (req, res) => {
    try {
        const { group_id, students } = req.body;
        if (!students || !Array.isArray(students)) {
            return res.status(400).json({ error: 'students должен быть массивом' });
        }
        const createdUsers = [];
        for (const student of students) {
            const hashedPassword = await bcrypt.hash(student.password, 10);
            const result = await pool.query(
                `INSERT INTO users (full_name, email, password_hash, role_id, group_id)
                 VALUES ($1, $2, $3, 1, $4) RETURNING user_id, full_name, email, group_id`,
                [student.full_name, student.email, hashedPassword, group_id]
            );
            createdUsers.push(result.rows[0]);
        }
        res.json({ message: 'Студенты успешно добавлены', users: createdUsers });
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Ошибка массового добавления' });
    }
});


// 2. ВЫПАДАЮЩИЕ СПИСКИ (DROPDOWNS) ДЛЯ ФОРМЫ РАСПИСАНИЯ

// Получить список преподавателей (роль 2)
router.get('/teachers', authMiddleware(allowedRoles), async (req, res) => {
    try {
        const result = await pool.query('SELECT user_id, full_name FROM users WHERE role_id = 2 ORDER BY full_name ASC');
        res.json(result.rows);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Получить список всех групп
router.get('/groups', authMiddleware(allowedRoles), async (req, res) => {
    try {
        const result = await pool.query('SELECT group_id, group_name FROM groups ORDER BY group_name ASC');
        res.json(result.rows);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});


// 3. УПРАВЛЕНИЕ РАСПИСАНИЕМ (LESSONS)

// Получить все пары с JOIN-ами имён преподавателей и групп
router.get('/lessons', authMiddleware(allowedRoles), async (req, res) => {
    try {
        const queryText = `
            SELECT 
                l.lesson_id, 
                l.subject, 
                l.start_time, 
                l.end_time, 
                l.room,
                u.full_name AS teacher_name,
                g.group_name
            FROM lessons l
            LEFT JOIN users u ON l.teacher_id = u.user_id
            LEFT JOIN groups g ON l.group_id = g.group_id
            ORDER BY l.start_time DESC
        `;
        const result = await pool.query(queryText);
        res.json(result.rows);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Добавить новую пару в расписание
router.post('/lessons', authMiddleware(allowedRoles), async (req, res) => {
    const { teacher_id, group_id, subject, start_time, end_time, room } = req.body;
    try {
        const result = await pool.query(
            'INSERT INTO lessons (teacher_id, group_id, subject, start_time, end_time, room) VALUES ($1, $2, $3, $4, $5, $6) RETURNING *',
            [teacher_id, group_id, subject, start_time, end_time, room]
        );
        res.json(result.rows[0]);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Удалить пару из расписания
router.delete('/lessons/:id', authMiddleware(allowedRoles), async (req, res) => {
    try {
        await pool.query('DELETE FROM lessons WHERE lesson_id = $1', [req.params.id]);
        res.json({ message: 'Пара успешно удалена из расписания' });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

module.exports = router;