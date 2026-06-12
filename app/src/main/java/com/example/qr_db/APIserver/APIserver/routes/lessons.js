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