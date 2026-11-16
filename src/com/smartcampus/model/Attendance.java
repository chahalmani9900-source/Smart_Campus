package com.smartcampus.model;

import java.util.Objects;

/**
 * Represents a daily attendance record for a student in a course.
 * Implements equals/hashCode for duplicate detection in LinkedHashSet.
 */
public class Attendance {
    private int attendanceId;
    private String studentId;
    private String courseCode;
    private String attendanceDate; // Format: YYYY-MM-DD
    private String status;         // PRESENT or ABSENT

    public Attendance(int attendanceId, String studentId, String courseCode,
                      String attendanceDate, String status) {
        this.attendanceId = attendanceId;
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.attendanceDate = attendanceDate;
        this.status = status.toUpperCase();
    }

    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(String attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status.toUpperCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Attendance)) return false;
        Attendance that = (Attendance) o;
        return Objects.equals(studentId.toUpperCase(), that.studentId.toUpperCase())
                && Objects.equals(courseCode.toUpperCase(), that.courseCode.toUpperCase())
                && Objects.equals(attendanceDate, that.attendanceDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId.toUpperCase(), courseCode.toUpperCase(), attendanceDate);
    }

    @Override
    public String toString() {
        return String.format("ID: %-4d | Student: %-8s | Course: %-10s | Date: %-10s | Status: %s",
                attendanceId, studentId, courseCode, attendanceDate, status);
    }
}
