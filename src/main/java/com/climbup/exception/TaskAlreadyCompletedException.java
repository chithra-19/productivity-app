package com.climbup.exception;

/**
 * Exception thrown when a task or challenge is already completed.
 */
public class TaskAlreadyCompletedException extends RuntimeException {

    // Default constructor
    public TaskAlreadyCompletedException() {
        super("Task or challenge has already been completed.");
    }

    // Constructor with custom message
    public TaskAlreadyCompletedException(String message) {
        super(message);
    }

    // Constructor with message and cause
    public TaskAlreadyCompletedException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor with cause
    public TaskAlreadyCompletedException(Throwable cause) {
        super(cause);
    }
}
