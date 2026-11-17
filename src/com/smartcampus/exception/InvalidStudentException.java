package com.smartcampus.exception;

/**
 * Custom checked exception thrown when student input or student ID is invalid.
 */
public class InvalidStudentException extends Exception {
    public InvalidStudentException(String message) {
        super(message);
    }
}
