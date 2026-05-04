const jwt = require('jsonwebtoken');
const SECRET = "supersecretkey";

module.exports = function (roles = []) {
    return (req, res, next) => {
        const token = req.headers['authorization'];

        if (!token) return res.status(401).send('Нет токена');

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