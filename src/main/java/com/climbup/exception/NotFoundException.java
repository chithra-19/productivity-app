package com.climbup.exception;

// ✅ Custom runtime exception for not found entities
public class NotFoundException extends RuntimeException {

    // Constructor with message
    public NotFoundException(String message) {
        super(message);
    }

    // Optional: constructor with message and cause
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
