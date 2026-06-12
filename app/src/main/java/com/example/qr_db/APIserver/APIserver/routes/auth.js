const express = require('express');
const router = express.Router();
const pool = require('../db'); 
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');

// ЛОГИН
router.post('/login', async (req, res) => {
    try {
        const { email, password } = req.body;

        const user = await pool.query(
            `SELECT * FROM users WHERE email = $1`,
            [email]
        );

        if (user.rows.length === 0) {
            return res.status(404).send('Пользователь не найден');
        }

        const validPassword = await bcrypt.compare(
            password,
            user.rows[0].password_hash
        );

        if (!validPassword) {
            return res.status(401).send('Неверный пароль');
        }

        const token = jwt.sign(
            {
                user_id: user.rows[0].user_id,
                role_id: user.rows[0].role_id
            },
            'smartcheck_secret',
            { expiresIn: '12h' }
        );

        res.json({
            message: 'Успешный вход',
            token
        });

    } catch (err) {
        console.error(err.message);
        res.status(500).send('Ошибка входа');
    }
});


//РЕГИСТРАЦИЯ
router.post('/register', async (req, res) => {

    try {

        const { full_name, email, password, role_id } = req.body;

        const hashedPassword = await bcrypt.hash(password, 10);

        const result = await pool.query(
            `
            INSERT INTO users
            (
                full_name,
                email,
                password_hash,
                role_id
            )
            VALUES
            (
                $1,
                $2,
                $3,
                $4
            )
            RETURNING *
            `,
            [
                full_name,
                email,
                hashedPassword,
                role_id || 1
            ]
        );

        res.json(result.rows[0]);

    } catch (err) {

        console.error(err);
        res.status(500).send('Ошибка регистрации');

    }

});

module.exports = router;