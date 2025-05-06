const Database = require("better-sqlite3");

const db = new Database(":memory:"); // In-memory database for testing

db.exec(`
    CREATE TABLE users (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        email TEXT NOT NULL
    )
`);

module.exports = db;