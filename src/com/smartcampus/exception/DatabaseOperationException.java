package com.smartcampus.exception;

/**
 * Custom exception wrapping SQL or persistence failures cleanly.
 */
public class DatabaseOperationException extends Exception {
    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseOperationException(String message) {
        super(message);
    }
}
