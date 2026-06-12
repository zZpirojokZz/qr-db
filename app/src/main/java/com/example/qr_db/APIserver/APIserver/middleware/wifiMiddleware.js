module.exports = function() {
    return (req, res, next) => {
        // Получаем SSID, который прислал нам клиент в заголовках запроса
        const clientSSID = req.headers['x-client-ssid'];
        
        // Получаем эталонный SSID из переменных окружения (.env)
        const requiredSSID = process.env.COLLEGE_WIFI_SSID;

        console.log(`=== ПРОВЕРКА WI-FI ===`);
        console.log(`SSID клиента: ${clientSSID}`);
        console.log(`Требуемый SSID: ${requiredSSID}`);

        // Если в .env проверка не настроена, пропускаем (для гибкости)
        if (!requiredSSID) {
            return next();
        }

        // Проверяем, совпадает ли сеть
        if (!clientSSID || clientSSID !== requiredSSID) {
            return res.status(403).json({ 
                error: 'Доступ заблокирован: вы должны быть подключены к официальной Wi-Fi сети колледжа!' 
            });
        }

        // Если всё отлично, передаем управление дальше
        next();
    };
};