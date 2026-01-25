package com.salepilot.backend.exception;

/**
 * Exception thrown when a user attempts an action they don't have permission
 * for.
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
