const express = require("express");
const pool = require("../db");

const router = express.Router();

// ===========================================
// ⚠️ ВАЖНО: конкретные роуты ДО динамических /:teacher_id
// ===========================================


// Активное занятие (ВРЕМЕННО: ищет любое занятие на СЕГОДНЯ)
router.get('/active', async (req, res) => {

    const { groupName, subject } = req.query;

    console.log('=== /lessons/active ===');
    console.log('groupName:', JSON.stringify(groupName));
    console.log('subject:', JSON.stringify(subject));

    try {
        const result = await pool.query(`
            SELECT l.lesson_id, l.subject, l.start_time, l.end_time,
                   g.group_id, g.group_name
            FROM lessons l
            JOIN groups g ON l.group_id = g.group_id
            WHERE g.group_name = $1
              AND l.subject = $2
            ORDER BY l.start_time DESC
            LIMIT 1
        `, [groupName, subject]);

        console.log('Найдено строк:', result.rows.length);
        if (result.rows.length > 0) {
            console.log('Данные:', JSON.stringify(result.rows[0]));
        }

        if (result.rows.length === 0) return res.json(null);
        res.json(result.rows[0]);

    } catch (err) {
        console.error('Ошибка /active:', err);
        res.status(500).json({ error: 'Ошибка сервера' });
    }
});

// Найти занятие по группе/предмету/дате
router.get('/find', async (req, res) => {

    const { groupName, subject, date } = req.query;

    try {
        const result = await pool.query(`
            SELECT l.lesson_id, l.subject, l.start_time, l.end_time,
                   g.group_id, g.group_name
            FROM lessons l
            JOIN groups g ON l.group_id = g.group_id
            WHERE g.group_name = $1
              AND l.subject = $2
              AND DATE(l.start_time) = $3
            LIMIT 1
        `, [groupName, subject, date]);

        if (result.rows.length === 0) return res.json(null);
        res.json(result.rows[0]);

    } catch (err) {
        console.error('Ошибка /find:', err);
        res.status(500).json({ error: 'Ошибка сервера' });
    }
});

// Список студентов группы по имени
router.get('/group-students/:groupName', async (req, res) => {

    const groupName = req.params.groupName;

    try {
        const result = await pool.query(`
            SELECT u.user_id, u.full_name
            FROM users u
            JOIN group_students gs ON gs.student_id = u.user_id
            JOIN groups g ON g.group_id = gs.group_id
            WHERE g.group_name = $1
            ORDER BY u.full_name ASC
        `, [groupName]);

        res.json(result.rows);

    } catch (err) {
        console.error('Ошибка /group-students:', err);
        res.status(500).json({ error: 'Ошибка сервера' });
    }
});

// Недельные оценки группы по предмету
router.get('/weekly-grades', async (req, res) => {

    const { groupName, subject, startDate } = req.query;

    try {
        const result = await pool.query(`
            SELECT
                u.user_id,
                u.full_name,
                DATE(l.start_time) as lesson_date,
                g.grade,
                g.attendance,
                l.lesson_id
            FROM users u
            JOIN group_students gs ON gs.student_id = u.user_id
            JOIN groups grp ON grp.group_id = gs.group_id
            LEFT JOIN lessons l
                ON l.group_id = grp.group_id
                AND l.subject = $2
                AND DATE(l.start_time) BETWEEN $3::date AND ($3::date + INTERVAL '6 days')
            LEFT JOIN grades g
                ON g.student_id = u.user_id
                AND g.lesson_id = l.lesson_id
            WHERE grp.group_name = $1
            ORDER BY u.full_name, l.start_time
        `, [groupName, subject, startDate]);

        res.json(result.rows);

    } catch (err) {
        console.error('Ошибка /weekly-grades:', err);
        res.status(500).json({ error: 'Ошибка сервера' });
    }
});

// Поставить/обновить оценку студенту
router.post('/set-grade', async (req, res) => {

    const { lesson_id, student_id, grade, attendance } = req.body;

    try {
        const existing = await pool.query(
            'SELECT grade_id FROM grades WHERE lesson_id = $1 AND student_id = $2',
            [lesson_id, student_id]
        );

        if (existing.rows.length > 0) {
            await pool.query(
                `UPDATE grades
                 SET grade = $1, attendance = $2
                 WHERE lesson_id = $3 AND student_id = $4`,
                [grade, attendance ?? true, lesson_id, student_id]
            );
        } else {
            await pool.query(
                `INSERT INTO grades (lesson_id, student_id, grade, attendance)
                 VALUES ($1, $2, $3, $4)`,
                [lesson_id, student_id, grade, attendance ?? true]
            );
        }

        res.json({ ok: true });

    } catch (err) {
        console.error('Ошибка /set-grade:', err);
        res.status(500).json({ error: 'Ошибка сервера' });
    }
});

// Текущая пара студента
router.get('/student/current-lesson/:id', async (req, res) => {

    const studentId = req.params.id;

    try {
        const result = await pool.query(`
            SELECT l.lesson_id, l.subject, g.group_name
            FROM lessons l
            JOIN group_students gs ON l.group_id = gs.group_id
            JOIN groups g ON l.group_id = g.group_id
            WHERE gs.student_id = $1
              AND l.start_time <= NOW()
              AND l.end_time >= NOW()
            LIMIT 1
        `, [studentId]);

        if (result.rows.length === 0) return res.json(null);
        res.json(result.rows[0]);

    } catch (err) {
        console.error('Ошибка /student/current-lesson:', err);
        res.status(500).json({ error: 'Ошибка сервера' });
    }
});

// Уникальные предметы группы по имени группы
router.get('/group-subjects/:groupName', async (req, res) => {

    const groupName = req.params.groupName;

    try {
        const result = await pool.query(`
            SELECT DISTINCT l.subject
            FROM lessons l
            JOIN groups g ON l.group_id = g.group_id
            WHERE g.group_name = $1
            ORDER BY l.subject ASC
        `, [groupName]);

        res.json(result.rows.map(r => r.subject));

    } catch (err) {
        console.error('Ошибка /group-subjects:', err);
        res.status(500).json({ error: 'Ошибка сервера' });
    }
});



// Сегодняшние пары преподавателя
router.get('/teacher-today/:teacher_id', async (req, res) => {

    const teacherId = req.params.teacher_id;

    try {
        const result = await pool.query(`
            SELECT l.lesson_id, l.subject, l.start_time, l.end_time,
                   g.group_id, g.group_name, l.room
            FROM lessons l
            JOIN groups g ON l.group_id = g.group_id
            WHERE l.teacher_id = $1
              AND DATE(l.start_time) = CURRENT_DATE
            ORDER BY l.start_time ASC
        `, [teacherId]);

        res.json(result.rows);

    } catch (err) {
        console.error('Ошибка /teacher-today:', err);
        res.status(500).json({ error: 'Ошибка сервера' });
    }
});


router.get("/:teacher_id", async (req, res) => {
    try {
        const result = await pool.query(
            "SELECT * FROM lessons WHERE teacher_id=$1",
            [req.params.teacher_id]
        );
        res.json(result.rows);
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Ошибка сервера' });
    }
});

module.exports = router;