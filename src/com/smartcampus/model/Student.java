package com.smartcampus.model;

/**
 * Represents an enrolled university student.
 * Demonstrates Inheritance (extends Person), Polymorphism, Wrapper classes, and Comparable.
 *
 * @author Manish Chahal
 * @author Moulik
 */
public class Student extends Person implements Comparable<Student> {
    private Integer semester;
    private Double gpa;
    private String phone;

    public Student(String studentId, String fullName, String email, String department,
                   Integer semester, Double gpa, String phone) {
        super(studentId, fullName, email, department);
        this.semester = semester;
        this.gpa = gpa;
        this.phone = phone;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Double getGpa() {
        return gpa;
    }

    public void setGpa(Double gpa) {
        this.gpa = gpa;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String getRoleSummary() {
        return String.format("Student [%s] %s - Sem %d (GPA: %.2f)",
                getId(), getFullName(), semester, gpa);
    }

    @Override
    public int compareTo(Student other) {
        return this.getId().compareToIgnoreCase(other.getId());
    }

    @Override
    public String toString() {
        return String.format("%s | Sem: %d | GPA: %5.2f | Phone: %s",
                super.toString(), semester, gpa, phone);
    }
}
