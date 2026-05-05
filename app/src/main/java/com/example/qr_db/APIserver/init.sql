-- ===============================
-- SmartCheck Database Init
-- ===============================

-- ===== TABLES =====

CREATE TABLE roles (
    role_id SERIAL PRIMARY KEY,
    role_name VARCHAR(20) UNIQUE
);

CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    password_hash TEXT NOT NULL,
    role_id INTEGER REFERENCES roles(role_id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE groups (
    group_id SERIAL PRIMARY KEY,
    group_name VARCHAR(20) UNIQUE,
    starosta_id INTEGER REFERENCES users(user_id)
);

CREATE TABLE lessons (
    lesson_id SERIAL PRIMARY KEY,
    teacher_id INTEGER REFERENCES users(user_id),
    group_id INTEGER REFERENCES groups(group_id),
    subject VARCHAR(100),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    room VARCHAR(20)
);

CREATE TABLE grades (
    grade_id SERIAL PRIMARY KEY,
    lesson_id INTEGER REFERENCES lessons(lesson_id),
    student_id INTEGER REFERENCES users(user_id),
    grade INTEGER CHECK (grade >= 0 AND grade <= 100),
    attendance BOOLEAN,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_student_lesson UNIQUE (student_id, lesson_id)
);

CREATE TABLE group_students (
    id SERIAL PRIMARY KEY,
    group_id INTEGER REFERENCES groups(group_id),
    student_id INTEGER REFERENCES users(user_id)
);

CREATE TABLE student_qr (
    qr_id SERIAL PRIMARY KEY,
    student_id INTEGER UNIQUE REFERENCES users(user_id),
    qr_data TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===== TRIGGER FUNCTION =====

CREATE OR REPLACE FUNCTION check_lesson_time()
RETURNS TRIGGER AS $$
DECLARE
    lesson_start TIMESTAMP;
    lesson_end TIMESTAMP;
BEGIN
    SELECT start_time, end_time
    INTO lesson_start, lesson_end
    FROM lessons
    WHERE lesson_id = NEW.lesson_id;

    IF NOW() < lesson_start OR NOW() > lesson_end THEN
        RAISE EXCEPTION 'Нельзя ставить оценку вне времени пары';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_lesson_time_trigger
BEFORE INSERT OR UPDATE
ON grades
FOR EACH ROW
EXECUTE FUNCTION check_lesson_time();

-- ===== DEFAULT ROLES =====

INSERT INTO roles (role_id, role_name) VALUES
(1, 'student'),
(2, 'teacher'),
(3, 'admin');