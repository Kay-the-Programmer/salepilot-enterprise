package com.salepilot.backend.service;

import com.salepilot.backend.config.AppProperties;
import com.salepilot.backend.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails including verification and password reset emails.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final AppProperties appProperties;

    /**
     * Send email verification link to user
     */
    @Async
    public void sendVerificationEmail(User user, String token) {
        if (!appProperties.getEmail().isEnabled()) {
            log.info("Email sending is disabled. Skipping verification email for: {}", user.getEmail());
            return;
        }

        String verificationUrl = String.format(
                "%s/verify-email?token=%s",
                appProperties.getEmail().getVerification().getBaseUrl(),
                token);

        String subject = "Verify your SalePilot account";
        String htmlContent = buildVerificationEmailHtml(user, verificationUrl);

        sendHtmlEmail(user.getEmail(), subject, htmlContent);
    }

    /**
     * Send password reset email to user
     */
    @Async
    public void sendPasswordResetEmail(User user, String token) {
        if (!appProperties.getEmail().isEnabled()) {
            log.info("Email sending is disabled. Skipping password reset email for: {}", user.getEmail());
            return;
        }

        String resetUrl = String.format(
                "%s/reset-password?token=%s",
                appProperties.getEmail().getVerification().getBaseUrl(),
                token);

        String subject = "Reset your SalePilot password";
        String htmlContent = buildPasswordResetEmailHtml(user, resetUrl);

        sendHtmlEmail(user.getEmail(), subject, htmlContent);
    }

    /**
     * Send welcome email to new user
     */
    @Async
    public void sendWelcomeEmail(User user) {
        if (!appProperties.getEmail().isEnabled()) {
            log.info("Email sending is disabled. Skipping welcome email for: {}", user.getEmail());
            return;
        }

        String subject = "Welcome to SalePilot!";
        String htmlContent = buildWelcomeEmailHtml(user);

        sendHtmlEmail(user.getEmail(), subject, htmlContent);
    }

    /**
     * Send HTML email
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(appProperties.getEmail().getFrom(), appProperties.getEmail().getFromName());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}. Error: {}", to, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error sending email to: {}. Error: {}", to, e.getMessage());
        }
    }

    /**
     * Build verification email HTML template
     */
    private String buildVerificationEmailHtml(User user, String verificationUrl) {
        return String.format(
                """
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>Verify Your Email</title>
                        </head>
                        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
                            <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
                                <h1 style="color: white; margin: 0;">SalePilot</h1>
                            </div>
                            <div style="background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px;">
                                <h2 style="color: #667eea;">Verify Your Email Address</h2>
                                <p>Hi %s,</p>
                                <p>Thank you for signing up with SalePilot! Please verify your email address by clicking the button below:</p>
                                <div style="text-align: center; margin: 30px 0;">
                                    <a href="%s" style="background: #667eea; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block;">Verify Email</a>
                                </div>
                                <p>Or copy and paste this link into your browser:</p>
                                <p style="background: #fff; padding: 10px; border-radius: 5px; word-break: break-all;"><a href="%s">%s</a></p>
                                <p style="color: #666; font-size: 12px; margin-top: 30px;">If you didn't create an account with SalePilot, you can safely ignore this email.</p>
                            </div>
                            <div style="text-align: center; margin-top: 20px; color: #999; font-size: 12px;">
                                <p>&copy; 2026 SalePilot. All rights reserved.</p>
                            </div>
                        </body>
                        </html>
                        """,
                user.getFullName(),
                verificationUrl,
                verificationUrl,
                verificationUrl);
    }

    /**
     * Build password reset email HTML template
     */
    private String buildPasswordResetEmailHtml(User user, String resetUrl) {
        return String.format(
                """
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>Reset Your Password</title>
                        </head>
                        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
                            <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
                                <h1 style="color: white; margin: 0;">SalePilot</h1>
                            </div>
                            <div style="background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px;">
                                <h2 style="color: #667eea;">Reset Your Password</h2>
                                <p>Hi %s,</p>
                                <p>We received a request to reset your password. Click the button below to create a new password:</p>
                                <div style="text-align: center; margin: 30px 0;">
                                    <a href="%s" style="background: #667eea; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block;">Reset Password</a>
                                </div>
                                <p>Or copy and paste this link into your browser:</p>
                                <p style="background: #fff; padding: 10px; border-radius: 5px; word-break: break-all;"><a href="%s">%s</a></p>
                                <p style="color: #e74c3c; font-weight: bold;">This password reset link will expire in 1 hour.</p>
                                <p style="color: #666; font-size: 12px; margin-top: 30px;">If you didn't request a password reset, you can safely ignore this email. Your password will remain unchanged.</p>
                            </div>
                            <div style="text-align: center; margin-top: 20px; color: #999; font-size: 12px;">
                                <p>&copy; 2026 SalePilot. All rights reserved.</p>
                            </div>
                        </body>
                        </html>
                        """,
                user.getFullName(),
                resetUrl,
                resetUrl,
                resetUrl);
    }

    /**
     * Build welcome email HTML template
     */
    private String buildWelcomeEmailHtml(User user) {
        return String.format(
                """
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>Welcome to SalePilot</title>
                        </head>
                        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
                            <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
                                <h1 style="color: white; margin: 0;">Welcome to SalePilot!</h1>
                            </div>
                            <div style="background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px;">
                                <h2 style="color: #667eea;">Let's Get Started</h2>
                                <p>Hi %s,</p>
                                <p>Welcome to SalePilot! We're excited to have you on board. Here's what you can do with your new account:</p>
                                <ul style="line-height: 2;">
                                    <li>📊 Manage your sales and transactions</li>
                                    <li>📦 Track your inventory in real-time</li>
                                    <li>👥 Manage customers and suppliers</li>
                                    <li>📈 View detailed analytics and reports</li>
                                    <li>🤖 Get AI-powered business insights</li>
                                </ul>
                                <div style="text-align: center; margin: 30px 0;">
                                    <a href="%s" style="background: #667eea; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block;">Go to Dashboard</a>
                                </div>
                                <p>If you have any questions, feel free to reach out to our support team.</p>
                            </div>
                            <div style="text-align: center; margin-top: 20px; color: #999; font-size: 12px;">
                                <p>&copy; 2026 SalePilot. All rights reserved.</p>
                            </div>
                        </body>
                        </html>
                        """,
                user.getFullName(),
                appProperties.getEmail().getVerification().getBaseUrl());
    }
}
