require('dotenv').config();
const express = require('express');
const cors = require('cors');
const pool = require('./db');
const app = express();

// Middleware
app.use(express.json());
app.use(cors({
  origin: ['http://smartcheck.aspc.kz'],
  credentials: true
}));

// Health Check
app.get("/health", (req, res) => {
  res.status(200).json({
    status: "ok",
    service: "SmartCheck API",
    timestamp: new Date().toISOString()
  });
});

// Main page
app.get('/', (req, res) => {
    res.send('API works');
});

// Routes
const authRoutes = require('./routes/auth');
app.use('/auth', authRoutes);

const usersRoutes = require('./routes/users');
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

// 404 Handler (must be LAST)
app.use((req, res) => {
  res.status(404).json({
    error: "Route not found"
  });
});

// Global Error Handler
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({
    error: "Internal server error"
  });
});

// Start server
const PORT = process.env.PORT || 3000;
app.listen(PORT, '0.0.0.0', () => {
    console.log('Server is running on http://0.0.0.0:' + PORT);
});