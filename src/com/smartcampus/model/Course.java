package com.smartcampus.model;

/**
 * Represents an academic course offered on campus.
 */
public class Course implements Comparable<Course> {
    private String courseCode;
    private String courseName;
    private String department;
    private Integer credits;
    private String facultyId;

    public Course(String courseCode, String courseName, String department,
                  Integer credits, String facultyId) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.department = department;
        this.credits = credits;
        this.facultyId = facultyId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }

    /** Compares courses by their course code in case-insensitive order. */
    @Override
    public int compareTo(Course other) {
        return this.courseCode.compareToIgnoreCase(other.courseCode);
    }

    @Override
    public String toString() {
        return String.format("%-11s | %-30s | %-22s | Credits: %d | Faculty: %s",
                courseCode, courseName, department, credits, facultyId);
    }
}
