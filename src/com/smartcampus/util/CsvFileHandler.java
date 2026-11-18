package com.smartcampus.util;

import com.smartcampus.model.Attendance;
import com.smartcampus.model.CampusEvent;
import com.smartcampus.model.Course;
import com.smartcampus.model.Faculty;
import com.smartcampus.model.Student;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles CSV File Reading and Writing using BufferedReader, BufferedWriter,
 * StringBuilder, and try-with-resources.
 */
public class CsvFileHandler {

    private static void ensureParentDir(String filePath) {
        File file = new File(filePath);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
    }

    // --- STUDENT CSV OPERATIONS ---
    public static void writeStudentsToCsv(String filePath, List<Student> students) throws IOException {
        ensureParentDir(filePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("student_id,full_name,email,department,semester,gpa,phone");
            writer.newLine();
            for (Student s : students) {
                StringBuilder sb = new StringBuilder();
                sb.append(s.getId()).append(",")
                  .append(s.getFullName().replace(",", " ")).append(",")
                  .append(s.getEmail()).append(",")
                  .append(s.getDepartment().replace(",", " ")).append(",")
                  .append(s.getSemester()).append(",")
                  .append(s.getGpa()).append(",")
                  .append(s.getPhone());
                writer.write(sb.toString());
                writer.newLine();
            }
        }
    }

    public static List<Student> readStudentsFromCsv(String filePath) throws IOException {
        List<Student> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return list;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    list.add(new Student(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            parts[3].trim(),
                            Integer.parseInt(parts[4].trim()),
                            Double.parseDouble(parts[5].trim()),
                            parts[6].trim()
                    ));
                }
            }
        }
        return list;
    }

    // --- FACULTY CSV OPERATIONS ---
    public static void writeFacultyToCsv(String filePath, List<Faculty> facultyList) throws IOException {
        ensureParentDir(filePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("faculty_id,full_name,email,department,designation,specialization");
            writer.newLine();
            for (Faculty f : facultyList) {
                writer.write(String.join(",",
                        f.getId(),
                        f.getFullName().replace(",", " "),
                        f.getEmail(),
                        f.getDepartment().replace(",", " "),
                        f.getDesignation().replace(",", " "),
                        f.getSpecialization().replace(",", " ")));
                writer.newLine();
            }
        }
    }

    public static List<Faculty> readFacultyFromCsv(String filePath) throws IOException {
        List<Faculty> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return list;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",");
                if (p.length >= 6) {
                    list.add(new Faculty(p[0].trim(), p[1].trim(), p[2].trim(),
                            p[3].trim(), p[4].trim(), p[5].trim()));
                }
            }
        }
        return list;
    }

    // --- COURSE CSV OPERATIONS ---
    public static void writeCoursesToCsv(String filePath, List<Course> courses) throws IOException {
        ensureParentDir(filePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("course_code,course_name,department,credits,faculty_id");
            writer.newLine();
            for (Course c : courses) {
                writer.write(String.join(",",
                        c.getCourseCode(),
                        c.getCourseName().replace(",", " "),
                        c.getDepartment().replace(",", " "),
                        String.valueOf(c.getCredits()),
                        c.getFacultyId()));
                writer.newLine();
            }
        }
    }

    public static List<Course> readCoursesFromCsv(String filePath) throws IOException {
        List<Course> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return list;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",");
                if (p.length >= 5) {
                    list.add(new Course(p[0].trim(), p[1].trim(), p[2].trim(),
                            Integer.parseInt(p[3].trim()), p[4].trim()));
                }
            }
        }
        return list;
    }

    // --- ATTENDANCE CSV OPERATIONS ---
    public static void writeAttendanceToCsv(String filePath, List<Attendance> records) throws IOException {
        ensureParentDir(filePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("attendance_id,student_id,course_code,attendance_date,status");
            writer.newLine();
            for (Attendance a : records) {
                writer.write(String.join(",",
                        String.valueOf(a.getAttendanceId()),
                        a.getStudentId(),
                        a.getCourseCode(),
                        a.getAttendanceDate(),
                        a.getStatus()));
                writer.newLine();
            }
        }
    }

    public static List<Attendance> readAttendanceFromCsv(String filePath) throws IOException {
        List<Attendance> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return list;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",");
                if (p.length >= 5) {
                    list.add(new Attendance(Integer.parseInt(p[0].trim()), p[1].trim(),
                            p[2].trim(), p[3].trim(), p[4].trim()));
                }
            }
        }
        return list;
    }

    // --- EVENTS CSV OPERATIONS ---
    public static void writeEventsToCsv(String filePath, List<CampusEvent> events) throws IOException {
        ensureParentDir(filePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("event_id,event_name,event_date,venue,organizer,description");
            writer.newLine();
            for (CampusEvent e : events) {
                writer.write(String.join(",",
                        e.getEventId(),
                        e.getEventName().replace(",", " "),
                        e.getEventDate(),
                        e.getVenue().replace(",", " "),
                        e.getOrganizer().replace(",", " "),
                        e.getDescription().replace(",", " ")));
                writer.newLine();
            }
        }
    }

    public static List<CampusEvent> readEventsFromCsv(String filePath) throws IOException {
        List<CampusEvent> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return list;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",");
                if (p.length >= 6) {
                    list.add(new CampusEvent(p[0].trim(), p[1].trim(), p[2].trim(),
                            p[3].trim(), p[4].trim(), p[5].trim()));
                }
            }
        }
        return list;
    }
}
