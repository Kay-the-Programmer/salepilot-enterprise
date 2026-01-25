package com.salepilot.backend.service;

import com.salepilot.backend.dto.request.LoginRequest;
import com.salepilot.backend.dto.request.RefreshTokenRequest;
import com.salepilot.backend.dto.request.RegisterRequest;
import com.salepilot.backend.dto.response.AuthResponse;

/**
 * Service interface for authentication operations.
 */
public interface AuthService {

    /**
     * Register a new user
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticate user and generate tokens
     */
    AuthResponse login(LoginRequest request);

    /**
     * Refresh access token using refresh token
     */
    AuthResponse refreshToken(RefreshTokenRequest request);

    /**
     * Logout user (invalidate tokens)
     */
    void logout(String token);
}
