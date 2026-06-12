const studentRoutes = require('./routes/student');
app.use('/student', studentRoutes);

// Группа студента
router.get('/student/:id/group', async (req, res) => {
    const studentId = req.params.id;
    try {
        const result = await pool.query(`
            SELECT g.group_id, g.group_name
            FROM group_students gs
            JOIN groups g ON l.group_id = g.group_id
              AND l.start_time <= $2          ← условие JOIN, а не WHERE!
              AND l.end_time >= $2
            LIMIT 1
        `, [studentId]);

        res.json(result.rows[0] || null);
    } catch (err) {
        console.error('Ошибка при получении группы:', err);
        res.status(500).json({ error: 'Ошибка сервера' });
    }
});

// Уникальные предметы для группы
router.get('/group/:groupId/subjects', async (req, res) => {
    const groupId = req.params.groupId;
    try {
        const result = await pool.query(`
            SELECT DISTINCT subject
            FROM lessons
            WHERE group_id = $1
            ORDER BY subject ASC
        `, [groupId]);

        res.json(result.rows.map(row => row.subject));
    } catch (err) {
        console.error('Ошибка при получении предметов группы:', err);
        res.status(500).json({ error: 'Ошибка сервера' });
    }
});