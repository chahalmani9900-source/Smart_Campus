package com.smartcampus.exception;

/**
 * Custom checked exception thrown when attendance parameters or status are invalid.
 */
public class InvalidAttendanceException extends Exception {
    public InvalidAttendanceException(String message) {
        super(message);
    }
}
