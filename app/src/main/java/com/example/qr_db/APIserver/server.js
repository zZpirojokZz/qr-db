const express = require('express');
const cors = require('cors');
const pool = require('./db');

const app = express();
app.use(express.json());
app.use(cors());

// --- ПОДКЛЮЧАЕМ РОУТЫ ---
const authRoutes = require('./routes/auth');
app.use('/auth', authRoutes);

const gradeRoutes = require('./routes/grades');
app.use('/grades', gradeRoutes);

const scheduleRoutes = require('./routes/schedule');
app.use('/schedule', scheduleRoutes);
const lessonsRoutes = require('./routes/lessons');     // ← ДОБАВЬ
app.use('/lessons', lessonsRoutes);


const adminRoutes = require('./routes/admin');
app.use('/admin', adminRoutes);

// --- БАЗОВЫЕ ПРОВЕРКИ ---
app.get('/test-db', async (req, res) => {
    try {
        const result = await pool.query('SELECT NOW()');
        res.json(result.rows);
    } catch (err) {
        console.error(err);
        res.status(500).send('Ошибка БД');
    }
});

app.get('/', (req, res) => {
    res.send('API работает');
});

// ЗАПУСК СЕРВЕРА
const PORT = 3000;
app.listen(PORT, '0.0.0.0', () => {
    console.log(`Server is running on http://0.0.0.0:${PORT}`);
});