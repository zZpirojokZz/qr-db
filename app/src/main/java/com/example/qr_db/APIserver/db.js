const { Pool } = require('pg');

const pool = new Pool({
    user: 'postgres',
    host: 'host.docker.internal',
    database: 'SmartCheck',
    password: '123456',
    port: 5432,
});

module.exports = pool;