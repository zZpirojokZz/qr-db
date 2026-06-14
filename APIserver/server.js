require('dotenv').config(); // Инициализация переменных окружения
const express = require('express');
const cors = require('cors');
const pool = require('./db');
const app = express();

// Подключаем глобальные middleware
app.use(express.json());
// CORS для защиты всех роуты
app.use(cors({
  origin: ['http://smartcheck.aspc.kz'],
  credentials: true
}));

//HEALTH CHECK
app.get("/health", (req, res) => {
  res.status(200).json({
    status: "ok",
    service: "SmartCheck API",
    timestamp: new Date().toISOString()
  });
});


// --- ПОДКЛЮЧАЕМ РОУТЫ ---
const authRoutes = require('./routes/auth');
app.use('/auth', authRoutes);

const usersRoutes = require('./routes/users.js');
app.use('/users', usersRoutes); 

const gradeRoutes = require('./routes/grades');
app.use('/grades', gradeRoutes);

const scheduleRoutes = require('./routes/schedule');
app.use('/schedule', scheduleRoutes);

const adminRoutes = require('./routes/admin');
app.use('/admin', adminRoutes);

const administrationRoutes = require('./routes/administration');
app.use('/administration', administrationRoutes);

const statisticsRoutes = require('./routes/statistics');
app.use('/statistics', statisticsRoutes);

const importRoutes = require('./routes/import');
app.use('/import', importRoutes); 

//404 HANDLER
app.use((req, res) => {
  res.status(404).json({
    error: "Route not found"
  });
});

//GLOBAL ERROR HANDLER
app.use((err, req, res, next) => {
  console.error(err.stack);

  res.status(500).json({
    error: "Internal server error"
  });
});

app.get('/', (req, res) => {
    res.send('API работает');
});

// ЗАПУСК СЕРВЕРА
const PORT = process.env.PORT || 3000;
app.listen(PORT, '0.0.0.0', () => {
    console.log(`Server is running on http://0.0.0.0:${PORT}`);
});