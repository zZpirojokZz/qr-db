const express = require('express');
const cors = require('cors');
const pool = require('./db');

const app = express();
app.use(express.json());

// Разрешает подключение с других устройств
app.use(cors());

// --- ПОДКЛЮЧАЕМ РОУТЫ ---
const authRoutes = require('./routes/auth');
app.use('/auth', authRoutes);

const gradeRoutes = require('./routes/grades');
app.use('/grades', gradeRoutes);

const scheduleRoutes = require('./routes/schedule');
app.use('/schedule', scheduleRoutes); 

// --- БАЗОВЫЕ ПРОВЕРКИ ---

// Тест подключения к БД (Я восстановил закрывающие скобки)
app.get('/test-db', async (req, res) => {
    try {
        const result = await pool.query('SELECT NOW()');
        res.json(result.rows);
    } catch (err) {
        console.error(err);
        res.status(500).send('Ошибка БД');
    }
});

// Покажет что API работает
app.get('/', (req, res) => {
    res.send('API работает');
});

// ЗАПУСК СЕРВЕРА
const PORT = 3000;
app.listen(PORT, '0.0.0.0', () => {
    console.log(`Server is running on http://0.0.0.0:${PORT}`);
    console.log(`To connect from phone, use your PC IP address on port ${PORT}`);
});