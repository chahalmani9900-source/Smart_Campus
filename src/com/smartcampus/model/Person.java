package com.smartcampus.model;

/**
 * Abstract base class representing a campus member (student or faculty).
 * Demonstrates Abstraction, Encapsulation, Constructors, and 'this' keyword.
 */
public abstract class Person {
    private String id;
    private String fullName;
    private String email;
    private String department;

    public Person(String id, String fullName, String email, String department) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.department = department;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * Abstract method overridden by subclasses to demonstrate Runtime Polymorphism.
     */
    public abstract String getRoleSummary();

    @Override
    public String toString() {
        return String.format("%-10s | %-20s | %-26s | %-22s", id, fullName, email, department);
    }
}
