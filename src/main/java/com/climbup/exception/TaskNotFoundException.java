package com.climbup.exception;

/**
 * Exception thrown when a task is not found in the database.
 */
public class TaskNotFoundException extends RuntimeException {

    // Default constructor
    public TaskNotFoundException() {
        super("Task not found.");
    }

    // Constructor with custom message
    public TaskNotFoundException(String message) {
        super(message);
    }

    // Constructor with message and cause
    public TaskNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor with cause
    public TaskNotFoundException(Throwable cause) {
        super(cause);
    }
}
