package com.salepilot.backend.service;

import com.salepilot.backend.entity.User;
import com.salepilot.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for user verification and password reset functionality
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Generate and send email verification token
     */
    @Transactional
    public void sendVerificationEmail(User user) {
        // Generate verification token
        String token = UUID.randomUUID().toString();

        // Save token to user
        user.setVerificationToken(token);
        user.setIsVerified(false);
        userRepository.save(user);

        // Send email
        emailService.sendVerificationEmail(user, token);

        log.info("Verification email sent to: {}", user.getEmail());
    }

    /**
     * Verify email with token
     */
    @Transactional
    public boolean verifyEmail(String token) {
        Optional<User> userOpt = userRepository.findByVerificationToken(token);

        if (userOpt.isEmpty()) {
            log.warn("Invalid verification token: {}", token);
            return false;
        }

        User user = userOpt.get();
        user.setIsVerified(true);
        user.setVerificationToken(null); // Clear token after verification
        userRepository.save(user);

        log.info("Email verified for user: {}", user.getEmail());
        return true;
    }

    /**
     * Resend verification email
     */
    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getIsVerified()) {
            throw new IllegalStateException("Email already verified");
        }

        sendVerificationEmail(user);
    }

    /**
     * Initiate password reset - generate token and send email
     */
    @Transactional
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        // Generate reset token
        String resetToken = UUID.randomUUID().toString();
        Instant expiryTime = Instant.now().plus(1, ChronoUnit.HOURS);

        // Save token and expiry
        user.setResetPasswordToken(resetToken);
        user.setResetPasswordExpires(expiryTime);
        userRepository.save(user);

        // Send email
        emailService.sendPasswordResetEmail(user, resetToken);

        log.info("Password reset email sent to: {}", email);
    }

    /**
     * Reset password using reset token
     */
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        Optional<User> userOpt = userRepository.findByResetPasswordToken(token);

        if (userOpt.isEmpty()) {
            log.warn("Invalid password reset token");
            return false;
        }

        User user = userOpt.get();

        // Check if token is expired
        if (user.getResetPasswordExpires().isBefore(Instant.now())) {
            log.warn("Password reset token expired for user: {}", user.getEmail());
            return false;
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordExpires(null);
        userRepository.save(user);

        log.info("Password reset successfully for user: {}", user.getEmail());
        return true;
    }

    /**
     * Check if user is verified
     */
    public boolean isUserVerified(String email) {
        return userRepository.findByEmail(email)
                .map(User::getIsVerified)
                .orElse(false);
    }
}
