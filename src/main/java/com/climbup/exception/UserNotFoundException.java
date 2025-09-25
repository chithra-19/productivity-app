package com.climbup.exception;

/**
 * Exception thrown when a user is not found in the database.
 */
public class UserNotFoundException extends RuntimeException {

    // Default constructor
    public UserNotFoundException() {
        super("User not found.");
    }

    // Constructor with custom message
    public UserNotFoundException(String message) {
        super(message);
    }

    // Constructor with message and cause
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor with cause
    public UserNotFoundException(Throwable cause) {
        super(cause);
    }
}
