-- ============================================================
-- Hostel Management System (HMS) - Database Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS hms_db;
USE hms_db;

-- ── Users (unified auth table) ────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    user_id       VARCHAR(20)  PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(100) UNIQUE NOT NULL,
    password      VARCHAR(255) NOT NULL,
    role          ENUM('STUDENT','WARDEN','MAINTENANCE') NOT NULL,
    created_at    DATETIME     DEFAULT CURRENT_TIMESTAMP
);

-- ── Students ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS students (
    student_id       VARCHAR(20) PRIMARY KEY,
    user_id          VARCHAR(20),
    room_number      VARCHAR(10),
    program          VARCHAR(50),
    contact_number   VARCHAR(15),
    outstanding_dues FLOAT       DEFAULT 0.0,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- ── Maintenance Staff ─────────────────────────────────────
CREATE TABLE IF NOT EXISTS maintenance_staff (
    staff_id        VARCHAR(20) PRIMARY KEY,
    user_id         VARCHAR(20),
    specialization  VARCHAR(50),
    contact_number  VARCHAR(15),
    is_available    BOOLEAN     DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- ── Complaints ────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS complaints (
    complaint_id  VARCHAR(20)  PRIMARY KEY,
    student_id    VARCHAR(20),
    description   VARCHAR(500) NOT NULL,
    category      VARCHAR(50)  NOT NULL,
    priority      ENUM('LOW','MEDIUM','HIGH')                    NOT NULL,
    status        ENUM('PENDING','ASSIGNED','IN_PROGRESS','RESOLVED') DEFAULT 'PENDING',
    submitted_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id)
);

-- ── Maintenance Tasks ─────────────────────────────────────
CREATE TABLE IF NOT EXISTS maintenance_tasks (
    task_id       VARCHAR(20) PRIMARY KEY,
    complaint_id  VARCHAR(20),
    staff_id      VARCHAR(20),
    status        ENUM('ASSIGNED','IN_PROGRESS','COMPLETED','CLOSED') DEFAULT 'ASSIGNED',
    assigned_at   DATETIME    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (complaint_id) REFERENCES complaints(complaint_id),
    FOREIGN KEY (staff_id)     REFERENCES maintenance_staff(staff_id)
);

-- ── Room Swap Requests ────────────────────────────────────
CREATE TABLE IF NOT EXISTS room_swap_requests (
    request_id     VARCHAR(20) PRIMARY KEY,
    student_id     VARCHAR(20),
    current_room   VARCHAR(10),
    requested_room VARCHAR(10),
    reason         VARCHAR(300) NOT NULL,
    status         ENUM('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING',
    FOREIGN KEY (student_id) REFERENCES students(student_id)
);

-- ── Visitor Log ───────────────────────────────────────────
CREATE TABLE IF NOT EXISTS visitor_log (
    visit_id      VARCHAR(20)  PRIMARY KEY,
    student_id    VARCHAR(20),
    visitor_name  VARCHAR(100) NOT NULL,
    visitor_cnic  VARCHAR(15)  NOT NULL,
    purpose       VARCHAR(200) NOT NULL,
    visit_date    DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id)
);

-- ============================================================
-- Seed Data for Testing
-- ============================================================

-- Users
INSERT IGNORE INTO users VALUES
  ('U001','Ali Hassan',   'ali@hms.com',  'pass123','STUDENT',     NOW()),
  ('U002','Sara Khan',    'sara@hms.com', 'pass123','STUDENT',     NOW()),
  ('U003','Zaid Mehmood', 'zaid@hms.com', 'pass123','MAINTENANCE', NOW()),
  ('U004','Raza Ahmed',   'raza@hms.com', 'pass123','MAINTENANCE', NOW());

-- Students
INSERT IGNORE INTO students VALUES
  ('S001','U001','101','BSCS','03001234567', 0.0),
  ('S002','U002','102','BSEE','03007654321', 500.0);

-- Maintenance Staff
INSERT IGNORE INTO maintenance_staff VALUES
  ('M001','U003','Plumbing',   '03111111111', TRUE),
  ('M002','U004','Electrical', '03222222222', TRUE);
