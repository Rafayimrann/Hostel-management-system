-- ============================================================
-- HMS Warden Module — Schema Additions
-- Run this AFTER the original schema.sql
-- ============================================================

USE hms_db;

-- ── Rooms (physical inventory for availability tracking) ───
CREATE TABLE IF NOT EXISTS rooms (
    room_number  VARCHAR(10)  PRIMARY KEY,
    capacity     INT          DEFAULT 2,
    status       ENUM('AVAILABLE','OCCUPIED','UNAVAILABLE') DEFAULT 'AVAILABLE',
    block        VARCHAR(20)  DEFAULT 'A'
);

-- ── Fine Policy (maps violation → amount) ─────────────────
CREATE TABLE IF NOT EXISTS fine_policy (
    violation_type VARCHAR(80)  PRIMARY KEY,
    amount         FLOAT        NOT NULL,
    description    VARCHAR(200)
);

-- ── Fines (issued by warden to student) ───────────────────
CREATE TABLE IF NOT EXISTS fines (
    fine_id        VARCHAR(20)  PRIMARY KEY,
    student_id     VARCHAR(20)  NOT NULL,
    violation_type VARCHAR(80)  NOT NULL,
    amount         FLOAT        NOT NULL,
    issued_by      VARCHAR(20)  NOT NULL,   -- warden user_id
    issued_at      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id)  REFERENCES students(student_id),
    FOREIGN KEY (issued_by)   REFERENCES users(user_id)
);

-- ── Notifications (simple in-app log) ─────────────────────
CREATE TABLE IF NOT EXISTS notifications (
    notif_id    VARCHAR(20)  PRIMARY KEY,
    recipient   VARCHAR(20)  NOT NULL,   -- user_id
    message     VARCHAR(500) NOT NULL,
    is_read     BOOLEAN      DEFAULT FALSE,
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (recipient) REFERENCES users(user_id)
);

-- ── Emergency Reallocation Log ────────────────────────────
CREATE TABLE IF NOT EXISTS emergency_reallocations (
    realloc_id    VARCHAR(20) PRIMARY KEY,
    student_id    VARCHAR(20) NOT NULL,
    old_room      VARCHAR(10) NOT NULL,
    new_room      VARCHAR(10) NOT NULL,
    reason        VARCHAR(300) NOT NULL,
    reallocated_by VARCHAR(20) NOT NULL,
    reallocated_at DATETIME   DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id)      REFERENCES students(student_id),
    FOREIGN KEY (reallocated_by)  REFERENCES users(user_id)
);

-- ============================================================
-- Seed Data
-- ============================================================

-- Warden user
INSERT IGNORE INTO users VALUES
  ('U005','Dr. Ayesha Malik','warden@hms.com','pass123','WARDEN', NOW());

-- Rooms (101–110)
INSERT IGNORE INTO rooms VALUES
  ('101', 2, 'OCCUPIED',   'A'),
  ('102', 2, 'OCCUPIED',   'A'),
  ('103', 2, 'AVAILABLE',  'A'),
  ('104', 2, 'AVAILABLE',  'A'),
  ('105', 2, 'AVAILABLE',  'B'),
  ('106', 2, 'AVAILABLE',  'B'),
  ('107', 2, 'UNAVAILABLE','B'),
  ('108', 2, 'AVAILABLE',  'B'),
  ('109', 2, 'AVAILABLE',  'C'),
  ('110', 2, 'AVAILABLE',  'C');

-- Fine policy table
INSERT IGNORE INTO fine_policy VALUES
  ('Late Night Curfew Violation', 500.0,  'Returning after 11 PM curfew'),
  ('Smoking on Premises',         1000.0, 'Smoking in non-designated area'),
  ('Property Damage',             2000.0, 'Intentional or negligent property damage'),
  ('Noise Violation',             300.0,  'Excessive noise during quiet hours'),
  ('Unauthorised Guest',          700.0,  'Unregistered guest staying overnight'),
  ('Kitchen Misuse',              400.0,  'Leaving kitchen unclean or misusing appliances'),
  ('Parking Violation',           200.0,  'Vehicle parked in unauthorised area'),
  ('Academic Misconduct',         1500.0, 'Violation of academic integrity on premises');
