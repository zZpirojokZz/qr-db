// Получить текущую пару преподавателя
router.get('/teacher/current-lesson/:id', async (req, res) => {
    const teacherId = req.params.id;
    const now = new Date().toISOString();

    try {
        const result = await pool.query(`
            SELECT
                l.lesson_id,
                l.teacher_id,
                l.group_id,
                l.subject,
                l.start_time,
                l.end_time,
                l.room,
                g.group_name
            FROM lessons l
            JOIN groups g ON l.group_id = g.group_id
              AND l.start_time <= $2
              AND l.end_time >= $2
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