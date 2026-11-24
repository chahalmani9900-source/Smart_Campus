package com.smartcampus.service;

import com.smartcampus.exception.DatabaseOperationException;
import com.smartcampus.exception.InvalidAttendanceException;
import com.smartcampus.model.Attendance;
import com.smartcampus.model.CampusEvent;
import com.smartcampus.model.Course;
import com.smartcampus.model.Faculty;
import com.smartcampus.model.Student;
import com.smartcampus.util.CsvFileHandler;
import com.smartcampus.util.DatabaseConnection;
import com.smartcampus.util.InputValidator;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Coordinates academic operations using basic JDBC or local CSV files.
 *  Manages faculty, courses, attendance records, and campus events. */
public class CampusAcademicService {
    private static final String FACULTY_CSV = "data/faculty.csv";
    private static final String COURSE_CSV = "data/courses.csv";
    private static final String ATTENDANCE_CSV = "data/attendance.csv";
    private static final String EVENTS_CSV = "data/events.csv";

    public CampusAcademicService() {
        initializeSampleDataIfEmpty();
    }

    private void initializeSampleDataIfEmpty() {
        if (DatabaseConnection.isMysqlAvailable()) return;
        try {
            List<Faculty> faculty = CsvFileHandler.readFacultyFromCsv(FACULTY_CSV);
            if (faculty.isEmpty()) {
                faculty.add(new Faculty("FAC201", "Dr. Meera Kulkarni", "meera.k@university.edu", "Computer Science", "Associate Professor", "Object-Oriented Systems & Java"));
                faculty.add(new Faculty("FAC202", "Prof. Rajeshwari Rao", "rajeshwari.r@university.edu", "Computer Science", "Assistant Professor", "Database Management Systems"));
                faculty.add(new Faculty("FAC203", "Dr. Vikramaditya Sen", "vikram.s@university.edu", "Electronics", "Professor", "Embedded Systems"));
                faculty.add(new Faculty("FAC204", "Prof. Neha Deshmukh", "neha.d@university.edu", "Information Technology", "Assistant Professor", "Data Structures & Algorithms"));
                CsvFileHandler.writeFacultyToCsv(FACULTY_CSV, faculty);
            }
            List<Course> courses = CsvFileHandler.readCoursesFromCsv(COURSE_CSV);
            if (courses.isEmpty()) {
                courses.add(new Course("25CSE0202", "Programming in Java", "Computer Science", 4, "FAC201"));
                courses.add(new Course("25CSE0204", "Database Management Systems", "Computer Science", 4, "FAC202"));
                courses.add(new Course("25ECE0201", "Digital Logic Design", "Electronics", 3, "FAC203"));
                courses.add(new Course("25ITE0203", "Data Structures in Practice", "Information Technology", 4, "FAC204"));
                CsvFileHandler.writeCoursesToCsv(COURSE_CSV, courses);
            }
            List<Attendance> attendance = CsvFileHandler.readAttendanceFromCsv(ATTENDANCE_CSV);
            if (attendance.isEmpty()) {
                attendance.add(new Attendance(1, "STU101", "25CSE0202", "2026-11-10", "PRESENT"));
                attendance.add(new Attendance(2, "STU102", "25CSE0202", "2026-11-10", "PRESENT"));
                attendance.add(new Attendance(3, "STU103", "25ECE0201", "2026-11-10", "ABSENT"));
                attendance.add(new Attendance(4, "STU104", "25ITE0203", "2026-11-11", "PRESENT"));
                attendance.add(new Attendance(5, "STU101", "25CSE0204", "2026-11-11", "PRESENT"));
                attendance.add(new Attendance(6, "STU102", "25CSE0204", "2026-11-11", "ABSENT"));
                CsvFileHandler.writeAttendanceToCsv(ATTENDANCE_CSV, attendance);
            }
            List<CampusEvent> events = CsvFileHandler.readEventsFromCsv(EVENTS_CSV);
            if (events.isEmpty()) {
                events.add(new CampusEvent("EVT301", "Annual Tech Symposium - Innovate 2026", "2026-11-25", "Main Auditorium", "CSE Department", "Inter-college coding hackathon and project showcase."));
                events.add(new CampusEvent("EVT302", "Database & JDBC Hands-on Workshop", "2026-12-02", "Computer Lab 3", "ACM Student Chapter", "Practical workshop on relational schema design and JDBC."));
                events.add(new CampusEvent("EVT303", "Winter Cultural & Sports Fest", "2026-12-15", "University Sports Complex", "Student Council", "Annual track events and evening cultural performances."));
                CsvFileHandler.writeEventsToCsv(EVENTS_CSV, events);
            }
        } catch (IOException ignored) {
        }
    }

    public void addFaculty(Faculty faculty) throws DatabaseOperationException {
        if (findFacultyById(faculty.getId()) != null) {
            throw new DatabaseOperationException("Faculty ID already exists: " + faculty.getId());
        }
        if (!DatabaseConnection.isMysqlAvailable()) {
            try {
                List<Faculty> list = CsvFileHandler.readFacultyFromCsv(FACULTY_CSV);
                list.add(faculty);
                CsvFileHandler.writeFacultyToCsv(FACULTY_CSV, list);
                return;
            } catch (IOException e) {
                throw new DatabaseOperationException("Error saving faculty to CSV: " + e.getMessage(), e);
            }
        }
        String sql = "INSERT INTO faculty (faculty_id, full_name, email, department, designation, specialization) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, faculty.getId()); statement.setString(2, faculty.getFullName());
            statement.setString(3, faculty.getEmail()); statement.setString(4, faculty.getDepartment());
            statement.setString(5, faculty.getDesignation()); statement.setString(6, faculty.getSpecialization());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error saving faculty to MySQL: " + e.getMessage(), e);
        }
    }

    public List<Faculty> getAllFaculty() throws DatabaseOperationException {
        if (!DatabaseConnection.isMysqlAvailable()) {
            try { return CsvFileHandler.readFacultyFromCsv(FACULTY_CSV); }
            catch (IOException e) { throw new DatabaseOperationException("Error reading faculty CSV: " + e.getMessage(), e); }
        }
        List<Faculty> list = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM faculty ORDER BY faculty_id"); ResultSet results = statement.executeQuery()) {
            while (results.next()) list.add(mapFaculty(results));
            return list;
        } catch (SQLException e) { throw new DatabaseOperationException("Error listing faculty: " + e.getMessage(), e); }
    }

    public boolean deleteFaculty(String facultyId) throws DatabaseOperationException {
        if (!DatabaseConnection.isMysqlAvailable()) {
            try {
                List<Faculty> list = CsvFileHandler.readFacultyFromCsv(FACULTY_CSV);
                boolean removed = list.removeIf(f -> f.getId().equalsIgnoreCase(facultyId));
                if (removed) CsvFileHandler.writeFacultyToCsv(FACULTY_CSV, list);
                return removed;
            } catch (IOException e) { throw new DatabaseOperationException("Error deleting faculty CSV: " + e.getMessage(), e); }
        }
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM faculty WHERE faculty_id = ?")) {
            statement.setString(1, facultyId); return statement.executeUpdate() > 0;
        } catch (SQLException e) { throw new DatabaseOperationException("Error deleting faculty (may be assigned to courses): " + e.getMessage(), e); }
    }

    public void addCourse(Course course) throws DatabaseOperationException {
        if (findCourseByCode(course.getCourseCode()) != null) throw new DatabaseOperationException("Course code already exists: " + course.getCourseCode());
        if (findFacultyById(course.getFacultyId()) == null) throw new DatabaseOperationException("Assigned Faculty ID does not exist: " + course.getFacultyId());
        if (!DatabaseConnection.isMysqlAvailable()) {
            try { List<Course> list = CsvFileHandler.readCoursesFromCsv(COURSE_CSV); list.add(course); CsvFileHandler.writeCoursesToCsv(COURSE_CSV, list); return; }
            catch (IOException e) { throw new DatabaseOperationException("Error saving course CSV: " + e.getMessage(), e); }
        }
        String sql = "INSERT INTO courses (course_code, course_name, department, credits, faculty_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, course.getCourseCode()); statement.setString(2, course.getCourseName());
            statement.setString(3, course.getDepartment()); statement.setInt(4, course.getCredits()); statement.setString(5, course.getFacultyId()); statement.executeUpdate();
        } catch (SQLException e) { throw new DatabaseOperationException("Error inserting course: " + e.getMessage(), e); }
    }

    public List<Course> getAllCourses() throws DatabaseOperationException {
        if (!DatabaseConnection.isMysqlAvailable()) {
            try { return CsvFileHandler.readCoursesFromCsv(COURSE_CSV); }
            catch (IOException e) { throw new DatabaseOperationException("Error reading courses CSV: " + e.getMessage(), e); }
        }
        List<Course> list = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM courses ORDER BY course_code"); ResultSet results = statement.executeQuery()) {
            while (results.next()) list.add(mapCourse(results)); return list;
        } catch (SQLException e) { throw new DatabaseOperationException("Error listing courses: " + e.getMessage(), e); }
    }

    public boolean deleteCourse(String courseCode) throws DatabaseOperationException {
        if (!DatabaseConnection.isMysqlAvailable()) {
            try { List<Course> list = CsvFileHandler.readCoursesFromCsv(COURSE_CSV); boolean removed = list.removeIf(c -> c.getCourseCode().equalsIgnoreCase(courseCode)); if (removed) CsvFileHandler.writeCoursesToCsv(COURSE_CSV, list); return removed; }
            catch (IOException e) { throw new DatabaseOperationException("Error deleting course CSV: " + e.getMessage(), e); }
        }
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM courses WHERE course_code = ?")) {
            statement.setString(1, courseCode); return statement.executeUpdate() > 0;
        } catch (SQLException e) { throw new DatabaseOperationException("Error deleting course: " + e.getMessage(), e); }
    }

    public void markAttendance(String studentId, String courseCode, String date, String status)
            throws InvalidAttendanceException, DatabaseOperationException {
        InputValidator.validateAttendanceData(studentId, courseCode, date, status);
        String normStudent = studentId.trim().toUpperCase(), normCourse = courseCode.trim().toUpperCase();
        if (findStudentById(normStudent) == null) throw new InvalidAttendanceException("Student ID does not exist: " + normStudent);
        if (findCourseByCode(normCourse) == null) throw new InvalidAttendanceException("Course Code does not exist: " + normCourse);
        String normalizedDate = date.trim();
        for (Attendance record : getAllAttendance()) {
            if (record.getStudentId().equalsIgnoreCase(normStudent)
                    && record.getCourseCode().equalsIgnoreCase(normCourse)
                    && record.getAttendanceDate().equals(normalizedDate)) {
                throw new InvalidAttendanceException("Attendance already marked for Student " + normStudent + " in " + normCourse + " on " + date);
            }
        }
        Attendance candidate = new Attendance(0, normStudent, normCourse, normalizedDate, status.trim().toUpperCase());
        if (!DatabaseConnection.isMysqlAvailable()) {
            try {
                List<Attendance> list = CsvFileHandler.readAttendanceFromCsv(ATTENDANCE_CSV);
                int nextId = list.stream().mapToInt(Attendance::getAttendanceId).max().orElse(0) + 1;
                candidate.setAttendanceId(nextId); list.add(candidate); CsvFileHandler.writeAttendanceToCsv(ATTENDANCE_CSV, list); return;
            } catch (IOException e) { throw new DatabaseOperationException("Error saving attendance CSV: " + e.getMessage(), e); }
        }
        String sql = "INSERT INTO attendance (student_id, course_code, attendance_date, status) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, candidate.getStudentId());
            statement.setString(2, candidate.getCourseCode());
            statement.setDate(3, Date.valueOf(candidate.getAttendanceDate()));
            statement.setString(4, candidate.getStatus());
            statement.executeUpdate();
        } catch (SQLException e) { throw new DatabaseOperationException("Error recording attendance in MySQL: " + e.getMessage(), e); }
    }

    public List<Attendance> getAllAttendance() throws DatabaseOperationException {
        if (!DatabaseConnection.isMysqlAvailable()) {
            try { return CsvFileHandler.readAttendanceFromCsv(ATTENDANCE_CSV); }
            catch (IOException e) { throw new DatabaseOperationException("Error reading attendance CSV: " + e.getMessage(), e); }
        }
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance ORDER BY attendance_date DESC, attendance_id DESC";
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet results = statement.executeQuery()) {
            while (results.next()) list.add(new Attendance(results.getInt("attendance_id"), results.getString("student_id"), results.getString("course_code"), results.getDate("attendance_date").toString(), results.getString("status")));
            return list;
        } catch (SQLException e) { throw new DatabaseOperationException("Error fetching attendance: " + e.getMessage(), e); }
    }

    public void addEvent(CampusEvent event) throws DatabaseOperationException {
        if (!DatabaseConnection.isMysqlAvailable()) {
            try { List<CampusEvent> list = CsvFileHandler.readEventsFromCsv(EVENTS_CSV); list.add(event); CsvFileHandler.writeEventsToCsv(EVENTS_CSV, list); return; }
            catch (IOException e) { throw new DatabaseOperationException("Error saving event CSV: " + e.getMessage(), e); }
        }
        String sql = "INSERT INTO campus_events (event_id, event_name, event_date, venue, organizer, description) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, event.getEventId()); statement.setString(2, event.getEventName()); statement.setDate(3, Date.valueOf(event.getEventDate()));
            statement.setString(4, event.getVenue()); statement.setString(5, event.getOrganizer()); statement.setString(6, event.getDescription()); statement.executeUpdate();
        } catch (SQLException e) { throw new DatabaseOperationException("Error adding campus event: " + e.getMessage(), e); }
    }

    public List<CampusEvent> getAllEvents() throws DatabaseOperationException {
        List<CampusEvent> events = new ArrayList<>();
        if (!DatabaseConnection.isMysqlAvailable()) {
            try { events.addAll(CsvFileHandler.readEventsFromCsv(EVENTS_CSV)); return events; }
            catch (IOException e) { throw new DatabaseOperationException("Error reading events CSV: " + e.getMessage(), e); }
        }
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM campus_events ORDER BY event_date ASC"); ResultSet results = statement.executeQuery()) {
            while (results.next()) events.add(new CampusEvent(results.getString("event_id"), results.getString("event_name"), results.getDate("event_date").toString(), results.getString("venue"), results.getString("organizer"), results.getString("description")));
            return events;
        } catch (SQLException e) { throw new DatabaseOperationException("Error loading events: " + e.getMessage(), e); }
    }

    public boolean deleteEvent(String eventId) throws DatabaseOperationException {
        if (!DatabaseConnection.isMysqlAvailable()) {
            try { List<CampusEvent> list = CsvFileHandler.readEventsFromCsv(EVENTS_CSV); boolean removed = list.removeIf(event -> event.getEventId().equalsIgnoreCase(eventId)); if (removed) CsvFileHandler.writeEventsToCsv(EVENTS_CSV, list); return removed; }
            catch (IOException e) { throw new DatabaseOperationException("Error deleting event CSV: " + e.getMessage(), e); }
        }
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM campus_events WHERE event_id = ?")) {
            statement.setString(1, eventId); return statement.executeUpdate() > 0;
        } catch (SQLException e) { throw new DatabaseOperationException("Error deleting event: " + e.getMessage(), e); }
    }

    private Faculty findFacultyById(String id) throws DatabaseOperationException {
        if (!DatabaseConnection.isMysqlAvailable()) {
            try { for (Faculty faculty : CsvFileHandler.readFacultyFromCsv(FACULTY_CSV)) if (faculty.getId().equalsIgnoreCase(id)) return faculty; return null; }
            catch (IOException e) { throw new DatabaseOperationException("Error reading faculty CSV: " + e.getMessage(), e); }
        }
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM faculty WHERE faculty_id = ?")) {
            statement.setString(1, id); try (ResultSet results = statement.executeQuery()) { return results.next() ? mapFaculty(results) : null; }
        } catch (SQLException e) { throw new DatabaseOperationException("Error finding faculty: " + e.getMessage(), e); }
    }

    private Course findCourseByCode(String code) throws DatabaseOperationException {
        if (!DatabaseConnection.isMysqlAvailable()) {
            try { for (Course course : CsvFileHandler.readCoursesFromCsv(COURSE_CSV)) if (course.getCourseCode().equalsIgnoreCase(code)) return course; return null; }
            catch (IOException e) { throw new DatabaseOperationException("Error reading courses CSV: " + e.getMessage(), e); }
        }
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM courses WHERE course_code = ?")) {
            statement.setString(1, code); try (ResultSet results = statement.executeQuery()) { return results.next() ? mapCourse(results) : null; }
        } catch (SQLException e) { throw new DatabaseOperationException("Error finding course: " + e.getMessage(), e); }
    }

    private Student findStudentById(String id) throws DatabaseOperationException {
        if (!DatabaseConnection.isMysqlAvailable()) {
            try { for (Student student : CsvFileHandler.readStudentsFromCsv("data/students.csv")) if (student.getId().equalsIgnoreCase(id)) return student; return null; }
            catch (IOException e) { throw new DatabaseOperationException("Error reading students CSV: " + e.getMessage(), e); }
        }
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM students WHERE student_id = ?")) {
            statement.setString(1, id); try (ResultSet results = statement.executeQuery()) { return results.next() ? new Student(results.getString("student_id"), results.getString("full_name"), results.getString("email"), results.getString("department"), results.getInt("semester"), results.getDouble("gpa"), results.getString("phone")) : null; }
        } catch (SQLException e) { throw new DatabaseOperationException("Error finding student: " + e.getMessage(), e); }
    }

    private Faculty mapFaculty(ResultSet r) throws SQLException { return new Faculty(r.getString("faculty_id"), r.getString("full_name"), r.getString("email"), r.getString("department"), r.getString("designation"), r.getString("specialization")); }
    private Course mapCourse(ResultSet r) throws SQLException { return new Course(r.getString("course_code"), r.getString("course_name"), r.getString("department"), r.getInt("credits"), r.getString("faculty_id")); }
}
