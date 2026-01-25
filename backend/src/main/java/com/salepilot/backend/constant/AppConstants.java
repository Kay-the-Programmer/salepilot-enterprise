package com.salepilot.backend.constant;

/**
 * Application-wide constants.
 */
public final class AppConstants {

    private AppConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // API Constants
    public static final String API_BASE_PATH = "/api/v1";
    public static final String DEFAULT_PAGE_SIZE = "20";
    public static final String MAX_PAGE_SIZE = "100";
    
    // Security Constants
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String TOKEN_TYPE = "JWT";
    public static final String AUTHORITIES_KEY = "roles";
    
    // Cache Names
    public static final String CACHE_USERS = "users";
    public static final String CACHE_PRODUCTS = "products";
    public static final String CACHE_ORDERS = "orders";
    
    // Role Constants
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_MANAGER = "ROLE_MANAGER";
    
    // Date Formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String ZONE_ID = "UTC";
    
    // Validation Messages
    public static final String VALIDATION_EMAIL_INVALID = "Email address is not valid";
    public static final String VALIDATION_FIELD_REQUIRED = "This field is required";
    public static final String VALIDATION_PASSWORD_MIN = "Password must be at least 8 characters";
    
    // Error Messages
    public static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";
    public static final String ERROR_UNAUTHORIZED = "Unauthorized access";
    public static final String ERROR_FORBIDDEN = "Access forbidden";
    public static final String ERROR_INTERNAL_SERVER = "Internal server error";
}
