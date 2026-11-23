package com.smartcampus.service;

import com.smartcampus.exception.DatabaseOperationException;
import com.smartcampus.exception.InvalidStudentException;
import com.smartcampus.model.Student;
import com.smartcampus.util.CsvFileHandler;
import com.smartcampus.util.DatabaseConnection;
import com.smartcampus.util.InputValidator;
import com.smartcampus.util.StudentNameComparator;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** Student management and persistence using basic JDBC or local CSV files.
 *  Supports full CRUD operations with duplicate detection. */
public class StudentService {
    private static final String CSV_PATH = "data/students.csv";

    public StudentService() {
        initializeSampleDataIfEmpty();
    }

    private void initializeSampleDataIfEmpty() {
        if (!DatabaseConnection.isMysqlAvailable()) {
            try {
                List<Student> existing = CsvFileHandler.readStudentsFromCsv(CSV_PATH);
                if (existing.isEmpty()) {
                    List<Student> seed = new ArrayList<>();
                    seed.add(new Student("STU101", "Aarav Sharma", "aarav.sharma@university.edu", "Computer Science", 3, 8.95, "9876543210"));
                    seed.add(new Student("STU102", "Diya Nair", "diya.nair@university.edu", "Computer Science", 3, 9.20, "9876543211"));
                    seed.add(new Student("STU103", "Rohan Verma", "rohan.verma@university.edu", "Electronics", 3, 8.15, "9876543212"));
                    seed.add(new Student("STU104", "Ananya Iyer", "ananya.iyer@university.edu", "Information Technology", 3, 9.40, "9876543213"));
                    seed.add(new Student("STU105", "Kabir Mehta", "kabir.mehta@university.edu", "Mechanical", 5, 7.85, "9876543214"));
                    CsvFileHandler.writeStudentsToCsv(CSV_PATH, seed);
                }
            } catch (IOException ignored) {
            }
        }
    }

    public void addStudent(String id, String name, String email, String department,
                           int semester, double gpa, String phone)
            throws InvalidStudentException, DatabaseOperationException {
        InputValidator.validateStudentData(id, name, email, department, semester, gpa, phone);
        String normalizedId = id.trim().toUpperCase();
        if (findStudentById(normalizedId) != null) {
            throw new InvalidStudentException("Duplicate Student ID detected: " + normalizedId + " already exists.");
        }
        Student student = new Student(normalizedId, name.trim(), email.trim(),
                department.trim(), semester, gpa, phone.trim());
        saveStudent(student);
    }

    public List<Student> getAllStudentsSortedById() throws DatabaseOperationException {
        List<Student> list = new ArrayList<>(findAllStudents());
        Collections.sort(list);
        return list;
    }

    public List<Student> getAllStudentsSortedByName() throws DatabaseOperationException {
        List<Student> list = new ArrayList<>(findAllStudents());
        list.sort(new StudentNameComparator());
        return list;
    }

    public Student searchStudentById(String studentId) throws InvalidStudentException, DatabaseOperationException {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new InvalidStudentException("Search Student ID cannot be empty.");
        }
        String key = studentId.trim().toUpperCase();
        Student found = findStudentById(key);
        if (found == null) {
            throw new InvalidStudentException("No student found with ID: " + key);
        }
        return found;
    }

    public void updateStudent(String id, String name, String email, String department,
                              int semester, double gpa, String phone)
            throws InvalidStudentException, DatabaseOperationException {
        InputValidator.validateStudentData(id, name, email, department, semester, gpa, phone);
        String normalizedId = id.trim().toUpperCase();
        if (findStudentById(normalizedId) == null) {
            throw new InvalidStudentException("Cannot update. Student ID not found: " + normalizedId);
        }
        Student updated = new Student(normalizedId, name.trim(), email.trim(),
                department.trim(), semester, gpa, phone.trim());
        updateStudentRecord(updated);
    }

    public void deleteStudent(String studentId) throws InvalidStudentException, DatabaseOperationException {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new InvalidStudentException("Student ID cannot be empty.");
        }
        String normalizedId = studentId.trim().toUpperCase();
        if (!deleteStudentRecord(normalizedId)) {
            throw new InvalidStudentException("Cannot delete. Student ID not found: " + normalizedId);
        }
    }

    public Map<String, List<Student>> groupStudentsByDepartment() throws DatabaseOperationException {
        return findAllStudents().stream()
                .collect(Collectors.groupingBy(Student::getDepartment, TreeMap::new, Collectors.toList()));
    }

    public void exportStudentsCsv(String exportPath) throws DatabaseOperationException, IOException {
        CsvFileHandler.writeStudentsToCsv(exportPath, getAllStudentsSortedById());
    }

    public int importStudentsCsv(String importPath) throws IOException, DatabaseOperationException {
        List<Student> imported = CsvFileHandler.readStudentsFromCsv(importPath);
        int addedCount = 0;
        for (Student student : imported) {
            try {
                addStudent(student.getId(), student.getFullName(), student.getEmail(), student.getDepartment(),
                        student.getSemester(), student.getGpa(), student.getPhone());
                addedCount++;
            } catch (InvalidStudentException ignored) {
                // Skip invalid or duplicate records during bulk CSV import.
            }
        }
        return addedCount;
    }

    private List<Student> findAllStudents() throws DatabaseOperationException {
        if (!DatabaseConnection.isMysqlAvailable()) {
            try {
                return CsvFileHandler.readStudentsFromCsv(CSV_PATH);
            } catch (IOException e) {
                throw new DatabaseOperationException("Error reading students CSV: " + e.getMessage(), e);
            }
        }
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY student_id";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) students.add(mapStudent(results));
            return students;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error fetching students from MySQL: " + e.getMessage(), e);
        }
    }

    private Student findStudentById(String id) throws DatabaseOperationException {
        if (!DatabaseConnection.isMysqlAvailable()) {
            try {
                for (Student student : CsvFileHandler.readStudentsFromCsv(CSV_PATH)) {
                    if (student.getId().equalsIgnoreCase(id)) return student;
                }
                return null;
            } catch (IOException e) {
                throw new DatabaseOperationException("Error reading students CSV: " + e.getMessage(), e);
            }
        }
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? mapStudent(results) : null;
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error searching student in MySQL: " + e.getMessage(), e);
        }
    }

    private void saveStudent(Student student) throws DatabaseOperationException {
        if (!DatabaseConnection.isMysqlAvailable()) {
            try {
                List<Student> students = CsvFileHandler.readStudentsFromCsv(CSV_PATH);
                students.add(student);
                CsvFileHandler.writeStudentsToCsv(CSV_PATH, students);
                return;
            } catch (IOException e) {
                throw new DatabaseOperationException("Failed to save student to CSV: " + e.getMessage(), e);
            }
        }
        String sql = "INSERT INTO students (student_id, full_name, email, department, semester, gpa, phone) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, student.getId());
            statement.setString(2, student.getFullName());
            statement.setString(3, student.getEmail());
            statement.setString(4, student.getDepartment());
            statement.setInt(5, student.getSemester());
            statement.setDouble(6, student.getGpa());
            statement.setString(7, student.getPhone());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to insert student into MySQL: " + e.getMessage(), e);
        }
    }

    private boolean updateStudentRecord(Student student) throws DatabaseOperationException {
        if (!DatabaseConnection.isMysqlAvailable()) {
            try {
                List<Student> students = CsvFileHandler.readStudentsFromCsv(CSV_PATH);
                boolean updated = false;
                for (int i = 0; i < students.size(); i++) {
                    if (students.get(i).getId().equalsIgnoreCase(student.getId())) {
                        students.set(i, student);
                        updated = true;
                        break;
                    }
                }
                if (updated) CsvFileHandler.writeStudentsToCsv(CSV_PATH, students);
                return updated;
            } catch (IOException e) {
                throw new DatabaseOperationException("Error updating student in CSV: " + e.getMessage(), e);
            }
        }
        String sql = "UPDATE students SET full_name=?, email=?, department=?, semester=?, gpa=?, phone=? WHERE student_id=?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, student.getFullName());
            statement.setString(2, student.getEmail());
            statement.setString(3, student.getDepartment());
            statement.setInt(4, student.getSemester());
            statement.setDouble(5, student.getGpa());
            statement.setString(6, student.getPhone());
            statement.setString(7, student.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error updating student in MySQL: " + e.getMessage(), e);
        }
    }

    private boolean deleteStudentRecord(String id) throws DatabaseOperationException {
        if (!DatabaseConnection.isMysqlAvailable()) {
            try {
                List<Student> students = CsvFileHandler.readStudentsFromCsv(CSV_PATH);
                boolean removed = students.removeIf(student -> student.getId().equalsIgnoreCase(id));
                if (removed) CsvFileHandler.writeStudentsToCsv(CSV_PATH, students);
                return removed;
            } catch (IOException e) {
                throw new DatabaseOperationException("Error deleting student in CSV: " + e.getMessage(), e);
            }
        }
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Error deleting student in MySQL: " + e.getMessage(), e);
        }
    }

    private Student mapStudent(ResultSet results) throws SQLException {
        return new Student(results.getString("student_id"), results.getString("full_name"),
                results.getString("email"), results.getString("department"), results.getInt("semester"),
                results.getDouble("gpa"), results.getString("phone"));
    }
}
