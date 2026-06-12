// В файле APIserver/routes/lessons.js
router.get("/teacher/:teacher_id", async (req, res) => {
  const result = await pool.query(
    `SELECT l.*, g.group_name 
     FROM lessons l 
     JOIN groups g ON l.group_id = g.group_id 
     WHERE l.teacher_id = $1`,
    [req.params.teacher_id]
  );
  res.json(result.rows);
});