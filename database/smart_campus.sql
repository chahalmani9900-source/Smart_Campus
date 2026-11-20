-- ============================================================================
-- SMART CAMPUS MANAGEMENT SYSTEM - DATABASE SCHEMA & SEED DATA (MySQL 8.0+)
-- Course: Programming in Java (25CSE0202) | 3rd Semester Individual Project
-- ============================================================================

CREATE DATABASE IF NOT EXISTS smart_campus_db;
USE smart_campus_db;

-- Drop existing tables in reverse dependency order
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS campus_events;
DROP TABLE IF EXISTS faculty;
DROP TABLE IF EXISTS students;

-- 1. STUDENTS TABLE
CREATE TABLE students (
    student_id VARCHAR(15) PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    department VARCHAR(60) NOT NULL,
    semester INT NOT NULL CHECK (semester BETWEEN 1 AND 8),
    gpa DECIMAL(4, 2) NOT NULL DEFAULT 0.00 CHECK (gpa BETWEEN 0.00 AND 10.00),
    phone VARCHAR(20) NOT NULL
);

-- 2. FACULTY TABLE
CREATE TABLE faculty (
    faculty_id VARCHAR(15) PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    department VARCHAR(60) NOT NULL,
    designation VARCHAR(60) NOT NULL,
    specialization VARCHAR(100) NOT NULL
);

-- 3. COURSES TABLE (Foreign Key -> faculty)
CREATE TABLE courses (
    course_code VARCHAR(15) PRIMARY KEY,
    course_name VARCHAR(120) NOT NULL,
    department VARCHAR(60) NOT NULL,
    credits INT NOT NULL CHECK (credits BETWEEN 1 AND 6),
    faculty_id VARCHAR(15) NOT NULL,
    CONSTRAINT fk_course_faculty FOREIGN KEY (faculty_id)
        REFERENCES faculty(faculty_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- 4. ATTENDANCE TABLE (Foreign Keys -> students, courses)
CREATE TABLE attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(15) NOT NULL,
    course_code VARCHAR(15) NOT NULL,
    attendance_date DATE NOT NULL,
    status ENUM('PRESENT', 'ABSENT') NOT NULL,
    CONSTRAINT uq_student_course_date UNIQUE (student_id, course_code, attendance_date),
    CONSTRAINT fk_att_student FOREIGN KEY (student_id)
        REFERENCES students(student_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_att_course FOREIGN KEY (course_code)
        REFERENCES courses(course_code)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- 5. CAMPUS EVENTS TABLE
CREATE TABLE campus_events (
    event_id VARCHAR(15) PRIMARY KEY,
    event_name VARCHAR(120) NOT NULL,
    event_date DATE NOT NULL,
    venue VARCHAR(100) NOT NULL,
    organizer VARCHAR(80) NOT NULL,
    description TEXT NOT NULL
);

-- ============================================================================
-- INITIAL SEED DATA FOR DEMONSTRATION
-- ============================================================================

INSERT INTO students (student_id, full_name, email, department, semester, gpa, phone) VALUES
('STU101', 'Aarav Sharma', 'aarav.sharma@university.edu', 'Computer Science', 3, 8.95, '9876543210'),
('STU102', 'Diya Nair', 'diya.nair@university.edu', 'Computer Science', 3, 9.20, '9876543211'),
('STU103', 'Rohan Verma', 'rohan.verma@university.edu', 'Electronics', 3, 8.15, '9876543212'),
('STU104', 'Ananya Iyer', 'ananya.iyer@university.edu', 'Information Technology', 3, 9.40, '9876543213'),
('STU105', 'Kabir Mehta', 'kabir.mehta@university.edu', 'Mechanical', 5, 7.85, '9876543214');

INSERT INTO faculty (faculty_id, full_name, email, department, designation, specialization) VALUES
('FAC201', 'Dr. Meera Kulkarni', 'meera.k@university.edu', 'Computer Science', 'Associate Professor', 'Object-Oriented Systems & Java'),
('FAC202', 'Prof. Rajeshwari Rao', 'rajeshwari.r@university.edu', 'Computer Science', 'Assistant Professor', 'Database Management Systems'),
('FAC203', 'Dr. Vikramaditya Sen', 'vikram.s@university.edu', 'Electronics', 'Professor', 'Embedded Systems'),
('FAC204', 'Prof. Neha Deshmukh', 'neha.d@university.edu', 'Information Technology', 'Assistant Professor', 'Data Structures & Algorithms');

INSERT INTO courses (course_code, course_name, department, credits, faculty_id) VALUES
('25CSE0202', 'Programming in Java', 'Computer Science', 4, 'FAC201'),
('25CSE0204', 'Database Management Systems', 'Computer Science', 4, 'FAC202'),
('25ECE0201', 'Digital Logic Design', 'Electronics', 3, 'FAC203'),
('25ITE0203', 'Data Structures in Practice', 'Information Technology', 4, 'FAC204');

INSERT INTO attendance (student_id, course_code, attendance_date, status) VALUES
('STU101', '25CSE0202', '2026-11-10', 'PRESENT'),
('STU102', '25CSE0202', '2026-11-10', 'PRESENT'),
('STU103', '25ECE0201', '2026-11-10', 'ABSENT'),
('STU104', '25ITE0203', '2026-11-11', 'PRESENT'),
('STU101', '25CSE0204', '2026-11-11', 'PRESENT'),
('STU102', '25CSE0204', '2026-11-11', 'ABSENT');

INSERT INTO campus_events (event_id, event_name, event_date, venue, organizer, description) VALUES
('EVT301', 'Annual Tech Symposium - Innovate 2026', '2026-11-25', 'Main Auditorium', 'CSE Department', 'Inter-college coding hackathon, project showcase, and technical paper presentations.'),
('EVT302', 'Database & JDBC Hands-on Workshop', '2026-12-02', 'Computer Lab 3', 'ACM Student Chapter', 'Practical workshop on relational schema design and JDBC connectivity.'),
('EVT303', 'Winter Cultural & Sports Fest', '2026-12-15', 'University Sports Complex', 'Student Council', 'Annual track events, basketball tournament, and evening cultural performances.');
