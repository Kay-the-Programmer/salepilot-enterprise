package com.salepilot.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standardized API error response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String errorCode;
    private Map<String, String> validationErrors;

    public static ErrorResponse of(int status, String error, String message, String path) {
        ErrorResponse response = new ErrorResponse();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(status);
        response.setError(error);
        response.setMessage(message);
        response.setPath(path);
        return response;
    }

    public static ErrorResponse of(int status, String error, String message, String path, String errorCode) {
        ErrorResponse response = of(status, error, message, path);
        response.setErrorCode(errorCode);
        return response;
    }

    public static ErrorResponse withValidation(int status, String error, String message, String path,
            Map<String, String> validationErrors) {
        ErrorResponse response = of(status, error, message, path);
        response.setValidationErrors(validationErrors);
        return response;
    }
}
