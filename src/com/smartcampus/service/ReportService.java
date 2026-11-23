package com.smartcampus.service;

import com.smartcampus.exception.DatabaseOperationException;
import com.smartcampus.model.Attendance;
import com.smartcampus.model.CampusEvent;
import com.smartcampus.model.Course;
import com.smartcampus.model.Faculty;
import com.smartcampus.model.Person;
import com.smartcampus.model.Student;
import com.smartcampus.util.CsvFileHandler;
import com.smartcampus.util.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Generates analytical campus reports using Java Lambda Expressions and Stream API.
 * Provides dashboard overview, honors list, department stats, and attendance summary.
 */
public class ReportService {
    public void printDashboardOverview() throws DatabaseOperationException {
        List<Student> students = findAllStudents();
        long totalStudents = students.size();
        long totalFaculty = findAllFaculty().size();
        long totalCourses = findAllCourses().size();
        long totalEvents = findAllEvents().size();

        List<Attendance> attendanceList = findAllAttendance();
        long presentCount = attendanceList.stream()
                .filter(a -> "PRESENT".equalsIgnoreCase(a.getStatus()))
                .count();
        double attendancePct = attendanceList.isEmpty() ? 0.0 : (presentCount * 100.0) / attendanceList.size();

        double avgGpa = students.stream()
                .mapToDouble(Student::getGpa)
                .average()
                .orElse(0.0);

        System.out.println("\n==================== CAMPUS DASHBOARD SUMMARY ====================");
        System.out.printf(" Total Students  : %-6d | Average Campus GPA : %.2f / 10.00%n", totalStudents, avgGpa);
        System.out.printf(" Total Faculty   : %-6d | Overall Attendance : %.1f%%%n", totalFaculty, attendancePct);
        System.out.printf(" Active Courses  : %-6d | Upcoming Events    : %d%n", totalCourses, totalEvents);
        System.out.println("==================================================================");
    }

    public void printHonorsStudentsReport(double minGpa) throws DatabaseOperationException {
        System.out.println("\n--- HONORS STUDENTS REPORT (GPA >= " + minGpa + ") [Stream filter + sorted] ---");
        List<Student> topStudents = findAllStudents().stream()
                .filter(s -> s.getGpa() >= minGpa)
                .sorted(Comparator.comparingDouble(Student::getGpa).reversed())
                .collect(Collectors.toList());

        if (topStudents.isEmpty()) {
            System.out.println("No students matched the GPA threshold.");
        } else {
            topStudents.forEach(s -> System.out.printf(" %-8s | %-20s | %-22s | GPA: %.2f%n",
                    s.getId(), s.getFullName(), s.getDepartment(), s.getGpa()));
        }
    }

    public void printDepartmentDistributionReport() throws DatabaseOperationException {
        System.out.println("\n--- DEPARTMENT-WISE STUDENT & COURSE SUMMARY [Stream groupingBy & distinct] ---");
        List<Student> students = findAllStudents();
        List<Course> courses = findAllCourses();

        List<String> distinctDepts = students.stream()
                .map(Student::getDepartment)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        Map<String, Long> studentCounts = students.stream()
                .collect(Collectors.groupingBy(Student::getDepartment, Collectors.counting()));

        for (String dept : distinctDepts) {
            long sCount = studentCounts.getOrDefault(dept, 0L);
            long cCount = courses.stream().filter(c -> c.getDepartment().equalsIgnoreCase(dept)).count();
            System.out.printf(" Department: %-24s | Students: %-3d | Courses Offered: %d%n", dept, sCount, cCount);
        }
    }

    public void printAttendanceSummaryReport() throws DatabaseOperationException {
        System.out.println("\n--- COURSE-WISE ATTENDANCE ANALYTICS [Stream Collectors] ---");
        List<Attendance> allRecords = findAllAttendance();
        Map<String, List<Attendance>> byCourse = allRecords.stream()
                .collect(Collectors.groupingBy(Attendance::getCourseCode));

        if (byCourse.isEmpty()) {
            System.out.println("No attendance records available.");
            return;
        }

        byCourse.forEach((courseCode, records) -> {
            long total = records.size();
            long present = records.stream().filter(r -> "PRESENT".equalsIgnoreCase(r.getStatus())).count();
            double pct = (present * 100.0) / total;
            System.out.printf(" Course: %-12s | Sessions Logged: %-3d | Present: %-3d | Rate: %.1f%%%n",
                    courseCode, total, present, pct);
        });
    }

    public void demonstratePolymorphicDirectory() throws DatabaseOperationException {
        System.out.println("\n--- UNIFIED CAMPUS DIRECTORY [Runtime Polymorphism: Person -> Student/Faculty] ---");
        List<Person> campusPeople = new ArrayList<>();
        campusPeople.addAll(findAllStudents());
        campusPeople.addAll(findAllFaculty());

        for (Person p : campusPeople) {
            System.out.println(" -> " + p.getRoleSummary());
        }
    }

    private List<Student> findAllStudents() throws DatabaseOperationException {
        if (!DatabaseConnection.isMysqlAvailable()) {
            try { return CsvFileHandler.readStudentsFromCsv("data/students.csv"); }
            catch (IOException e) { throw new DatabaseOperationException("Error reading students CSV: " + e.getMessage(), e); }
        }
        List<Student> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement s = c.prepareStatement("SELECT * FROM students ORDER BY student_id"); ResultSet r = s.executeQuery()) {
            while (r.next()) list.add(new Student(r.getString("student_id"), r.getString("full_name"), r.getString("email"), r.getString("department"), r.getInt("semester"), r.getDouble("gpa"), r.getString("phone")));
            return list;
        } catch (SQLException e) { throw new DatabaseOperationException("Error fetching students: " + e.getMessage(), e); }
    }

    private List<Faculty> findAllFaculty() throws DatabaseOperationException {
        if (!DatabaseConnection.isMysqlAvailable()) {
            try { return CsvFileHandler.readFacultyFromCsv("data/faculty.csv"); }
            catch (IOException e) { throw new DatabaseOperationException("Error reading faculty CSV: " + e.getMessage(), e); }
        }
        List<Faculty> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement s = c.prepareStatement("SELECT * FROM faculty ORDER BY faculty_id"); ResultSet r = s.executeQuery()) {
            while (r.next()) list.add(new Faculty(r.getString("faculty_id"), r.getString("full_name"), r.getString("email"), r.getString("department"), r.getString("designation"), r.getString("specialization")));
            return list;
        } catch (SQLException e) { throw new DatabaseOperationException("Error fetching faculty: " + e.getMessage(), e); }
    }

    private List<Course> findAllCourses() throws DatabaseOperationException {
        if (!DatabaseConnection.isMysqlAvailable()) {
            try { return CsvFileHandler.readCoursesFromCsv("data/courses.csv"); }
            catch (IOException e) { throw new DatabaseOperationException("Error reading courses CSV: " + e.getMessage(), e); }
        }
        List<Course> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement s = c.prepareStatement("SELECT * FROM courses ORDER BY course_code"); ResultSet r = s.executeQuery()) {
            while (r.next()) list.add(new Course(r.getString("course_code"), r.getString("course_name"), r.getString("department"), r.getInt("credits"), r.getString("faculty_id")));
            return list;
        } catch (SQLException e) { throw new DatabaseOperationException("Error fetching courses: " + e.getMessage(), e); }
    }

    private List<Attendance> findAllAttendance() throws DatabaseOperationException {
        if (!DatabaseConnection.isMysqlAvailable()) {
            try { return CsvFileHandler.readAttendanceFromCsv("data/attendance.csv"); }
            catch (IOException e) { throw new DatabaseOperationException("Error reading attendance CSV: " + e.getMessage(), e); }
        }
        List<Attendance> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement s = c.prepareStatement("SELECT * FROM attendance ORDER BY attendance_date DESC, attendance_id DESC"); ResultSet r = s.executeQuery()) {
            while (r.next()) list.add(new Attendance(r.getInt("attendance_id"), r.getString("student_id"), r.getString("course_code"), r.getDate("attendance_date").toString(), r.getString("status")));
            return list;
        } catch (SQLException e) { throw new DatabaseOperationException("Error fetching attendance: " + e.getMessage(), e); }
    }

    private List<CampusEvent> findAllEvents() throws DatabaseOperationException {
        if (!DatabaseConnection.isMysqlAvailable()) {
            try { return CsvFileHandler.readEventsFromCsv("data/events.csv"); }
            catch (IOException e) { throw new DatabaseOperationException("Error reading events CSV: " + e.getMessage(), e); }
        }
        List<CampusEvent> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement s = c.prepareStatement("SELECT * FROM campus_events ORDER BY event_date ASC"); ResultSet r = s.executeQuery()) {
            while (r.next()) list.add(new CampusEvent(r.getString("event_id"), r.getString("event_name"), r.getDate("event_date").toString(), r.getString("venue"), r.getString("organizer"), r.getString("description")));
            return list;
        } catch (SQLException e) { throw new DatabaseOperationException("Error fetching events: " + e.getMessage(), e); }
    }
}
