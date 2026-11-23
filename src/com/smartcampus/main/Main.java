package com.smartcampus.main;

import com.smartcampus.exception.DatabaseOperationException;
import com.smartcampus.exception.InvalidAttendanceException;
import com.smartcampus.exception.InvalidStudentException;
import com.smartcampus.model.Attendance;
import com.smartcampus.model.CampusEvent;
import com.smartcampus.model.Course;
import com.smartcampus.model.Faculty;
import com.smartcampus.model.Student;
import com.smartcampus.service.CampusAcademicService;
import com.smartcampus.service.ReportService;
import com.smartcampus.service.StudentService;
import com.smartcampus.util.DatabaseConnection;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/** The single entry point for the Smart Campus programs.
 *  Supports both web server mode and console interactive mode. */
public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length > 0 && "--console".equalsIgnoreCase(args[0])) {
            SmartCampusApp.runConsole();
            return;
        }

        Path webFolder = Paths.get("web").toAbsolutePath().normalize();

        if (!Files.exists(webFolder.resolve("index.html"))) {
            System.out.println("Open the terminal in the project folder and try again.");
            return;
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", exchange -> {
            String requestedFile = exchange.getRequestURI().getPath();
            if ("/".equals(requestedFile)) {
                requestedFile = "/index.html";
            }

            Path file = webFolder.resolve(requestedFile.substring(1)).normalize();
            if (!file.startsWith(webFolder) || !Files.isRegularFile(file)) {
                byte[] message = "Page not found".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(404, message.length);
                exchange.getResponseBody().write(message);
                exchange.close();
                return;
            }

            String fileName = file.getFileName().toString().toLowerCase();
            String contentType = "text/plain; charset=UTF-8";
            if (fileName.endsWith(".html")) contentType = "text/html; charset=UTF-8";
            if (fileName.endsWith(".css")) contentType = "text/css; charset=UTF-8";
            if (fileName.endsWith(".js")) contentType = "application/javascript; charset=UTF-8";
            if (fileName.endsWith(".png")) contentType = "image/png";

            byte[] content = Files.readAllBytes(file);
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, content.length);
            exchange.getResponseBody().write(content);
            exchange.close();
        });

        server.start();
        System.out.println("Smart Campus site is running at http://localhost:8080/");
        System.out.println("Press Ctrl+C to stop it.");
    }
}

class SmartCampusApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final StudentService studentService = new StudentService();
    private static final CampusAcademicService academicService = new CampusAcademicService();
    private static final ReportService reportService = new ReportService();

    public static void runConsole() {
        System.out.println("==================================================================");
        System.out.println("         SMART CAMPUS MANAGEMENT SYSTEM (Java 25CSE0202)          ");
        System.out.println("==================================================================");
        System.out.println(DatabaseConnection.getConnectionStatusMessage());

        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1":
                        reportService.printDashboardOverview();
                        break;
                    case "2":
                        handleStudentMenu();
                        break;
                    case "3":
                        handleFacultyMenu();
                        break;
                    case "4":
                        handleCourseMenu();
                        break;
                    case "5":
                        handleAttendanceMenu();
                        break;
                    case "6":
                        handleEventsMenu();
                        break;
                    case "7":
                        handleReportsMenu();
                        break;
                    case "0":
                        running = false;
                        System.out.println("Exiting Smart Campus Management System. Goodbye!");
                        break;
                    default:
                        System.out.println("[ERROR] Invalid option. Please enter a number between 0 and 7.");
                }
            } catch (DatabaseOperationException e) {
                System.out.println("[DATABASE ERROR] " + e.getMessage());
            } catch (Exception e) {
                System.out.println("[UNEXPECTED ERROR] " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static void printMainMenu() {
        System.out.println("\n------------------------- MAIN MENU -------------------------");
        System.out.println(" 1. Campus Dashboard Overview");
        System.out.println(" 2. Student Management (CRUD, Search, Sort, CSV)");
        System.out.println(" 3. Faculty Management");
        System.out.println(" 4. Course Management");
        System.out.println(" 5. Attendance Management");
        System.out.println(" 6. Campus Events");
        System.out.println(" 7. Reports & Stream Analytics");
        System.out.println(" 0. Exit");
        System.out.print("Enter your choice: ");
    }

    // ==================== MODULE 2: STUDENT MANAGEMENT ====================
    private static void handleStudentMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- STUDENT MANAGEMENT ---");
            System.out.println(" 1. Add New Student");
            System.out.println(" 2. Display All Students (Sorted by ID - Comparable)");
            System.out.println(" 3. Display All Students (Sorted by Name - Comparator)");
            System.out.println(" 4. Search Student by ID");
            System.out.println(" 5. Update Student Details");
            System.out.println(" 6. Delete Student");
            System.out.println(" 7. View Students Grouped by Department (TreeMap)");
            System.out.println(" 8. Export Students to CSV File");
            System.out.println(" 9. Import Students from CSV File");
            System.out.println(" 0. Back to Main Menu");
            System.out.print("Select option: ");
            String ch = scanner.nextLine().trim();

            try {
                switch (ch) {
                    case "1":
                        System.out.print("Student ID (e.g., STU106): ");
                        String id = scanner.nextLine();
                        System.out.print("Full Name: ");
                        String name = scanner.nextLine();
                        System.out.print("Email: ");
                        String email = scanner.nextLine();
                        System.out.print("Department: ");
                        String dept = scanner.nextLine();
                        System.out.print("Semester (1-8): ");
                        int sem = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("GPA (0.0 - 10.0): ");
                        double gpa = Double.parseDouble(scanner.nextLine().trim());
                        System.out.print("Phone (10 digits): ");
                        String phone = scanner.nextLine();

                        studentService.addStudent(id, name, email, dept, sem, gpa, phone);
                        System.out.println("[SUCCESS] Student added successfully!");
                        break;

                    case "2":
                        List<Student> byId = studentService.getAllStudentsSortedById();
                        printStudentTable(byId);
                        break;

                    case "3":
                        List<Student> byName = studentService.getAllStudentsSortedByName();
                        printStudentTable(byName);
                        break;

                    case "4":
                        System.out.print("Enter Student ID to search: ");
                        Student found = studentService.searchStudentById(scanner.nextLine());
                        System.out.println("[FOUND] " + found);
                        break;

                    case "5":
                        System.out.print("Enter existing Student ID to update: ");
                        String uId = scanner.nextLine();
                        Student existing = studentService.searchStudentById(uId);
                        System.out.println("Current Record: " + existing);
                        System.out.print("New Full Name: ");
                        String uName = scanner.nextLine();
                        System.out.print("New Email: ");
                        String uEmail = scanner.nextLine();
                        System.out.print("New Department: ");
                        String uDept = scanner.nextLine();
                        System.out.print("New Semester (1-8): ");
                        int uSem = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("New GPA (0.0 - 10.0): ");
                        double uGpa = Double.parseDouble(scanner.nextLine().trim());
                        System.out.print("New Phone (10 digits): ");
                        String uPhone = scanner.nextLine();

                        studentService.updateStudent(uId, uName, uEmail, uDept, uSem, uGpa, uPhone);
                        System.out.println("[SUCCESS] Student updated successfully!");
                        break;

                    case "6":
                        System.out.print("Enter Student ID to delete: ");
                        studentService.deleteStudent(scanner.nextLine());
                        System.out.println("[SUCCESS] Student deleted successfully!");
                        break;

                    case "7":
                        Map<String, List<Student>> grouped = studentService.groupStudentsByDepartment();
                        grouped.forEach((d, list) -> {
                            System.out.println("\nDepartment: " + d + " (" + list.size() + " students)");
                            list.forEach(s -> System.out.println("  - " + s));
                        });
                        break;

                    case "8":
                        String exportPath = "data/exported_students.csv";
                        studentService.exportStudentsCsv(exportPath);
                        System.out.println("[SUCCESS] Exported students to " + exportPath);
                        break;

                    case "9":
                        System.out.print("Enter CSV path to import (e.g., data/sample_import.csv): ");
                        String importPath = scanner.nextLine().trim();
                        int count = studentService.importStudentsCsv(importPath);
                        System.out.println("[SUCCESS] Imported " + count + " new valid student records.");
                        break;

                    case "0":
                        back = true;
                        break;

                    default:
                        System.out.println("[ERROR] Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println("[INPUT ERROR] Please enter a valid numeric value for Semester or GPA.");
            } catch (InvalidStudentException e) {
                System.out.println("[VALIDATION ERROR] " + e.getMessage());
            } catch (DatabaseOperationException | IOException e) {
                System.out.println("[PERSISTENCE ERROR] " + e.getMessage());
            }
        }
    }

    private static void printStudentTable(List<Student> students) {
        if (students.isEmpty()) {
            System.out.println("No student records found.");
            return;
        }
        System.out.println("\n-------------------------------------------------------------------------------------------------------------");
        students.forEach(System.out::println);
        System.out.println("-------------------------------------------------------------------------------------------------------------");
    }

    // ==================== MODULE 3: FACULTY MANAGEMENT ====================
    private static void handleFacultyMenu() throws DatabaseOperationException {
        System.out.println("\n--- FACULTY MANAGEMENT ---");
        System.out.println(" 1. View All Faculty");
        System.out.println(" 2. Add New Faculty");
        System.out.println(" 3. Delete Faculty");
        System.out.print("Select option: ");
        String ch = scanner.nextLine().trim();

        if ("1".equals(ch)) {
            List<Faculty> list = academicService.getAllFaculty();
            list.forEach(System.out::println);
        } else if ("2".equals(ch)) {
            System.out.print("Faculty ID (e.g., FAC205): ");
            String id = scanner.nextLine().trim().toUpperCase();
            System.out.print("Full Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Department: ");
            String dept = scanner.nextLine().trim();
            System.out.print("Designation: ");
            String desig = scanner.nextLine().trim();
            System.out.print("Specialization: ");
            String spec = scanner.nextLine().trim();
            academicService.addFaculty(new Faculty(id, name, email, dept, desig, spec));
            System.out.println("[SUCCESS] Faculty member added.");
        } else if ("3".equals(ch)) {
            System.out.print("Faculty ID to delete: ");
            boolean deleted = academicService.deleteFaculty(scanner.nextLine().trim());
            System.out.println(deleted ? "[SUCCESS] Faculty deleted." : "[ERROR] Faculty ID not found.");
        }
    }

    // ==================== MODULE 4: COURSE MANAGEMENT ====================
    private static void handleCourseMenu() throws DatabaseOperationException {
        System.out.println("\n--- COURSE MANAGEMENT ---");
        System.out.println(" 1. View All Courses");
        System.out.println(" 2. Add New Course");
        System.out.println(" 3. Delete Course");
        System.out.print("Select option: ");
        String ch = scanner.nextLine().trim();

        if ("1".equals(ch)) {
            List<Course> list = academicService.getAllCourses();
            list.forEach(System.out::println);
        } else if ("2".equals(ch)) {
            try {
                System.out.print("Course Code (e.g., 25CSE0208): ");
                String code = scanner.nextLine().trim().toUpperCase();
                System.out.print("Course Name: ");
                String name = scanner.nextLine().trim();
                System.out.print("Department: ");
                String dept = scanner.nextLine().trim();
                System.out.print("Credits (1-6): ");
                int credits = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("Assigned Faculty ID (e.g., FAC201): ");
                String facId = scanner.nextLine().trim().toUpperCase();
                academicService.addCourse(new Course(code, name, dept, credits, facId));
                System.out.println("[SUCCESS] Course added.");
            } catch (NumberFormatException e) {
                System.out.println("[INPUT ERROR] Credits must be an integer.");
            }
        } else if ("3".equals(ch)) {
            System.out.print("Course Code to delete: ");
            boolean deleted = academicService.deleteCourse(scanner.nextLine().trim());
            System.out.println(deleted ? "[SUCCESS] Course deleted." : "[ERROR] Course code not found.");
        }
    }

    // ==================== MODULE 5: ATTENDANCE MANAGEMENT ====================
    private static void handleAttendanceMenu() throws DatabaseOperationException {
        System.out.println("\n--- ATTENDANCE MANAGEMENT ---");
        System.out.println(" 1. Mark Student Attendance");
        System.out.println(" 2. View All Attendance Records");
        System.out.print("Select option: ");
        String ch = scanner.nextLine().trim();

        if ("1".equals(ch)) {
            try {
                System.out.print("Student ID (e.g., STU101): ");
                String stuId = scanner.nextLine();
                System.out.print("Course Code (e.g., 25CSE0202): ");
                String cCode = scanner.nextLine();
                System.out.print("Date (YYYY-MM-DD): ");
                String date = scanner.nextLine();
                System.out.print("Status (PRESENT / ABSENT): ");
                String status = scanner.nextLine();

                academicService.markAttendance(stuId, cCode, date, status);
                System.out.println("[SUCCESS] Attendance recorded.");
            } catch (InvalidAttendanceException e) {
                System.out.println("[ATTENDANCE ERROR] " + e.getMessage());
            }
        } else if ("2".equals(ch)) {
            List<Attendance> list = academicService.getAllAttendance();
            list.forEach(System.out::println);
        }
    }

    // ==================== MODULE 6: CAMPUS EVENTS ====================
    private static void handleEventsMenu() throws DatabaseOperationException {
        System.out.println("\n--- CAMPUS EVENTS ---");
        System.out.println(" 1. View Upcoming Events");
        System.out.println(" 2. Schedule New Event");
        System.out.println(" 3. Cancel Event");
        System.out.print("Select option: ");
        String ch = scanner.nextLine().trim();

        if ("1".equals(ch)) {
            for (CampusEvent ev : academicService.getAllEvents()) {
                System.out.println(ev);
                System.out.println("   Description: " + ev.getDescription());
            }
        } else if ("2".equals(ch)) {
            System.out.print("Event ID (e.g., EVT304): ");
            String id = scanner.nextLine().trim().toUpperCase();
            System.out.print("Event Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Event Date (YYYY-MM-DD): ");
            String date = scanner.nextLine().trim();
            System.out.print("Venue: ");
            String venue = scanner.nextLine().trim();
            System.out.print("Organizer: ");
            String org = scanner.nextLine().trim();
            System.out.print("Description: ");
            String desc = scanner.nextLine().trim();
            academicService.addEvent(new CampusEvent(id, name, date, venue, org, desc));
            System.out.println("[SUCCESS] Campus event scheduled.");
        } else if ("3".equals(ch)) {
            System.out.print("Event ID to cancel: ");
            boolean deleted = academicService.deleteEvent(scanner.nextLine().trim());
            System.out.println(deleted ? "[SUCCESS] Event cancelled." : "[ERROR] Event ID not found.");
        }
    }

    // ==================== MODULE 7: REPORTS & ANALYTICS ====================
    private static void handleReportsMenu() throws DatabaseOperationException {
        System.out.println("\n--- REPORTS & ANALYTICS (JAVA STREAM API) ---");
        System.out.println(" 1. Honors Students Report (GPA >= 8.5)");
        System.out.println(" 2. Department-Wise Distribution Report");
        System.out.println(" 3. Course Attendance Rate Summary");
        System.out.println(" 4. Unified Polymorphic Directory (Students + Faculty)");
        System.out.print("Select option: ");
        String ch = scanner.nextLine().trim();

        switch (ch) {
            case "1":
                reportService.printHonorsStudentsReport(8.5);
                break;
            case "2":
                reportService.printDepartmentDistributionReport();
                break;
            case "3":
                reportService.printAttendanceSummaryReport();
                break;
            case "4":
                reportService.demonstratePolymorphicDirectory();
                break;
            default:
                System.out.println("[ERROR] Invalid report choice.");
        }
    }
}

class BasicJdbcDemo {
    private static final String URL = "jdbc:mysql://localhost:3306/smart_campus_db";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    public void showStudents() throws SQLException {
        String sql = "SELECT student_id, student_name FROM jdbc_students ORDER BY student_id";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            System.out.println("\nID         Name");
            System.out.println("------------------------------");
            while (results.next()) {
                System.out.printf("%-10s %s%n", results.getString("student_id"),
                        results.getString("student_name"));
            }
        }
    }

    public void addStudent(String id, String name) throws SQLException {
        String sql = "INSERT INTO jdbc_students (student_id, student_name) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.setString(2, name);
            System.out.println(statement.executeUpdate() + " student added.");
        }
    }

    public void updateStudentName(String id, String name) throws SQLException {
        String sql = "UPDATE jdbc_students SET student_name = ? WHERE student_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, id);
            System.out.println(statement.executeUpdate() + " student updated.");
        }
    }

    public void deleteStudent(String id) throws SQLException {
        String sql = "DELETE FROM jdbc_students WHERE student_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            System.out.println(statement.executeUpdate() + " student deleted.");
        }
    }
}

