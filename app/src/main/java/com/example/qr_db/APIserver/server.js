const express = require('express');
const pool = require('./db');
const cors = require('cors');

const app = express();
app.use(express.json());

// Разрешает подключение с других устройств
app.use(cors());

// Подключаем роуты
const authRoutes = require('./routes/auth');
app.use('/auth', authRoutes);

const gradeRoutes = require('./routes/grades');
app.use('/grades', gradeRoutes);

// Тест подключения к БД
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
// Добавили '0.0.0.0', чтобы телефон мог подключиться к компьютеру по Wi-Fi
const PORT = 3000;
app.listen(PORT, '0.0.0.0', () => {
    console.log(`Server is running on http://0.0.0.0:${PORT}`);
    console.log(`To connect from phone, use your PC IP address on port ${PORT}`);
});
