const express = require("express");
const pool = require("../db");

const router = express.Router();

router.get("/:teacher_id", async (req, res) => {

  const result = await pool.query(
    "SELECT * FROM lessons WHERE teacher_id=$1",
    [req.params.teacher_id]
  );

  res.json(result.rows);

});

module.exports = router;


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

        if (result.rows.length === 0) {
            return res.json(null);
        }

        res.json(result.rows[0]);

    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Ошибка сервера' });
    }
});