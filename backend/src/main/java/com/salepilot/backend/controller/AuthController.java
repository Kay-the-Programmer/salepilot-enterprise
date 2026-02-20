package com.salepilot.backend.controller;

import com.salepilot.backend.constant.AppConstants;
import com.salepilot.backend.dto.request.LoginRequest;
import com.salepilot.backend.dto.request.RefreshTokenRequest;
import com.salepilot.backend.dto.request.RegisterRequest;
import com.salepilot.backend.dto.response.ApiResponse;
import com.salepilot.backend.dto.response.AuthResponse;
import com.salepilot.backend.service.AuthService;
import com.salepilot.backend.service.VerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints.
 */
@RestController
@RequestMapping(AppConstants.API_BASE_PATH + "/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication API endpoints")
public class AuthController {

    private final AuthService authService;
    private final VerificationService verificationService;

    @Operation(summary = "Register a new user", description = "Create a new user account")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User registered successfully", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("POST /auth/register - Registering user: {}", request.getUsername());

        AuthResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }

    @Operation(summary = "Login user", description = "Authenticate user and return JWT tokens")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /auth/login - Login attempt: {}", request.getUsernameOrEmail());

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @Operation(summary = "Refresh access token", description = "Get new access token using refresh token")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token refreshed successfully", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid refresh token")
    })
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("POST /auth/refresh - Refreshing token");

        AuthResponse response = authService.refreshToken(request);

        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    @Operation(summary = "Logout user", description = "Logout user and invalidate tokens")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Logout successful")
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String token) {
        log.info("POST /auth/logout - User logout");

        authService.logout(token);

        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
    }

    @Operation(summary = "Verify email", description = "Verify user email with token")
    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam("token") String token) {
        log.info("POST /auth/verify-email - Verifying email");

        boolean verified = verificationService.verifyEmail(token);

        if (verified) {
            return ResponseEntity.ok(ApiResponse.success("Email verified successfully", null));
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid or expired verification token"));
        }
    }

    @Operation(summary = "Resend verification email", description = "Resend verification email to user")
    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<Void>> resendVerification(@RequestParam("email") String email) {
        log.info("POST /auth/resend-verification - Resending verification email to: {}", email);

        verificationService.resendVerificationEmail(email);

        return ResponseEntity.ok(ApiResponse.success("Verification email sent", null));
    }

    @Operation(summary = "Forgot password", description = "Initiate password reset process")
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestParam("email") String email) {
        log.info("POST /auth/forgot-password - Password reset requested for: {}", email);

        verificationService.initiatePasswordReset(email);

        return ResponseEntity.ok(ApiResponse.success("Password reset email sent", null));
    }

    @Operation(summary = "Reset password", description = "Reset password using reset token")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestParam("token") String token,
            @RequestParam("password") String newPassword) {
        log.info("POST /auth/reset-password - Resetting password");

        boolean reset = verificationService.resetPassword(token, newPassword);

        if (reset) {
            return ResponseEntity.ok(ApiResponse.success("Password reset successfully", null));
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid or expired reset token"));
        }
    }
}
