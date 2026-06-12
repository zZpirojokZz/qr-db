const express = require('express');
const router = express.Router();
const multer = require('multer');
const XLSX = require('xlsx');
const fs = require('fs');
const path = require('path');
const pool = require('../db');
const authMiddleware = require('../middleware/authMiddleware');
const wifiMiddleware = require('../middleware/wifiMiddleware');

// ОПРЕДЕЛЯЕМ ПУТЬ К ПАПКЕ UPLOADS
const uploadDir = path.join(__dirname, '../uploads');

if (!fs.existsSync(uploadDir)) {
    fs.mkdirSync(uploadDir, { recursive: true });
}

// НАСТРОЙКА ХРАНИЛИЩА МУЛЬТЕРА
const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, uploadDir);
    },
    filename: (req, file, cb) => {
        cb(null, Date.now() + '-' + file.originalname);
    }
});

const upload = multer({ storage });


// 1. ИМПОРТ РАСПИСАНИЯ ИЗ EXCEL (POST)
router.post('/schedule', authMiddleware([4]), upload.single('file'), async (req, res) => {
    try {
        console.log('=== НАЧАЛО ИМПОРТА ===');
        console.log('Полученный файл:', req.file);

        if (!req.file) {
            return res.status(400).json({ error: 'Файл не загружен' });
        }

        const workbook = XLSX.readFile(req.file.path);
        const sheetName = workbook.SheetNames[0];
        const sheet = workbook.Sheets[sheetName];
        const data = XLSX.utils.sheet_to_json(sheet, { raw: false });

        console.log(`Найдено строк для импорта: ${data.length}`);

        for (let i = 0; i < data.length; i++) {
            const row = data[i];
            const rowNum = i + 2; // Номер строки в Excel (с учетом заголовка)

            const { group_name, teacher_id, subject, start_time, end_time, room } = row;

            // 1. ВАЛИДАЦИЯ: Проверяем обязательные поля
            if (!group_name || !teacher_id || !subject || !start_time || !end_time) {
                return res.status(400).json({ 
                    error: `Ошибка в строке ${rowNum}: Все поля (кроме кабинета) обязательны для заполнения!` 
                });
            }

            // 2. ПР ПРОВЕРКА КОНФЛИКТОВ: Преподаватель не может быть в двух местах одновременно
            const teacherConflict = await pool.query(
                `SELECT * FROM lessons 
                 WHERE teacher_id = $1 
                 AND NOT (end_time <= $2 OR start_time >= $3)`,
                [teacher_id, start_time, end_time]
            );

            if (teacherConflict.rows.length > 0) {
                return res.status(400).json({
                    error: `Конфликт в строке ${rowNum}: Преподаватель (ID: ${teacher_id}) уже ведет уроки в это время!`
                });
            }

            // 3. ПРОВЕРКА КОНФЛИКТОВ: Кабинет не может быть занят двумя группами одновременно
            if (room) {
                const roomConflict = await pool.query(
                    `SELECT * FROM lessons 
                     WHERE room = $1 
                     AND NOT (end_time <= $2 OR start_time >= $3)`,
                    [room, start_time, end_time]
                );

                if (roomConflict.rows.length > 0) {
                    return res.status(400).json({
                        error: `Конфликт в строке ${rowNum}: Кабинет ${room} уже занят в это время другой группой!`
                    });
                }
            }

            // 4. ПОИСК ИЛИ СОЗДАНИЕ ГРУППЫ
            let groupResult = await pool.query(
                'SELECT group_id FROM groups WHERE group_name = $1',
                [group_name]
            );

            let group_id;
            if (groupResult.rows.length === 0) {
                const newGroup = await pool.query(
                    'INSERT INTO groups(group_name) VALUES($1) RETURNING group_id',
                    [group_name]
                );
                group_id = newGroup.rows[0].group_id;
            } else {
                group_id = groupResult.rows[0].group_id;
            }

            // 5. ЗАПИСЬ В БД
            await pool.query(
                `INSERT INTO lessons (teacher_id, group_id, subject, start_time, end_time, room)
                 VALUES ($1, $2, $3, $4, $5, $6)`,
                [teacher_id, group_id, subject, start_time, end_time, room || null]
            );
        }

        console.log('=== ИМПОРТ ЗАВЕРШЕН УСПЕШНО ===');
        res.json({ message: 'Расписание успешно импортировано из файла Excel!' });
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Ошибка при импорте', details: err.message });
    }
});


// 2. РОУТ ДЛЯ СКАЧИВАНИЯ ШАБЛОНА EXCEL (GET)
router.get('/template', (req, res) => {
    const file = path.join(__dirname, 'template.xlsx'); 
    
    res.download(file, 'template_schedule.xlsx', (err) => {
        if (err) {
            console.error('Ошибка при скачивании шаблона:', err);
            if (!res.headersSent) {
                res.status(500).send('Не удалось скачать шаблон');
            }
        }
    });
});


// 3. РОУТ: СКАНИРОВАНИЕ ДЛЯ СТУДЕНТОВ (С ПРОВЕРКОЙ WI-FI)
router.get('/student-scan', wifiMiddleware(), async (req, res) => {
    try {
        // Если wifiMiddleware пропустил запрос сюда, значит проверка SSID пройдена успешно.
        // Сервер возвращает статус 200 OK и JSON-данные для приложения студента
        res.json({ 
            success: true,
            status: "verified",
            message: "Сканирование выполнено успешно. Устройство находится в сети Wi-Fi колледжа!",
            timestamp: new Date()
        });
    } catch (error) {
        res.status(500).json({ error: 'Ошибка при обработке сканирования', details: error.message });
    }
});

module.exports = router;