package com.messmanagement.exception;

/**
 * Thrown when a requested Student or Payment does not exist.
 * Mapped to a friendly error page / 404 JSON response.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
