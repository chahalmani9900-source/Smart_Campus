package com.smartcampus.model;

/**
 * Represents a university faculty member.
 * Demonstrates Inheritance (extends Person) and Method Overriding.
 */
public class Faculty extends Person {
    private String designation;
    private String specialization;

    public Faculty(String facultyId, String fullName, String email, String department,
                   String designation, String specialization) {
        super(facultyId, fullName, email, department);
        this.designation = designation;
        this.specialization = specialization;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public String getRoleSummary() {
        return String.format("Faculty [%s] %s (%s, %s)",
                getId(), getFullName(), designation, getDepartment());
    }

    @Override
    public String toString() {
        return String.format("%s | %-20s | %s",
                super.toString(), designation, specialization);
    }
}
