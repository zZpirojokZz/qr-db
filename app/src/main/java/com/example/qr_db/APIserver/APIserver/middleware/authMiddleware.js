const jwt = require('jsonwebtoken');
const SECRET = process.env.JWT_SECRET;

module.exports = function (roles = []) {
    return (req, res, next) => {
        let token = req.headers['authorization'];

        if (!token) return res.status(401).send('Нет токена');

        // ХАК: Если токен начинается со слова Bearer (в любом регистре), отрезаем его
        if (token.toLowerCase().startsWith('bearer ')) {
            token = token.slice(7).trim();
        }

        try {
            const decoded = jwt.verify(token, SECRET);

            if (roles.length && !roles.includes(decoded.role_id)) {
                return res.status(403).send('Нет доступа');
            }

            req.user = decoded;
            next();
        } catch (err) {
            res.status(401).send('Неверный токен');
        }
    };
};