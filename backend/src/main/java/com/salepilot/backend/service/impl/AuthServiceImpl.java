package com.salepilot.backend.service.impl;

import com.salepilot.backend.constant.AppConstants;
import com.salepilot.backend.dto.request.LoginRequest;
import com.salepilot.backend.dto.request.RefreshTokenRequest;
import com.salepilot.backend.dto.request.RegisterRequest;
import com.salepilot.backend.dto.response.AuthResponse;
import com.salepilot.backend.entity.Role;
import com.salepilot.backend.entity.User;
import com.salepilot.backend.exception.ConflictException;
import com.salepilot.backend.exception.UnauthorizedException;
import com.salepilot.backend.repository.RoleRepository;
import com.salepilot.backend.repository.UserRepository;
import com.salepilot.backend.security.JwtTokenProvider;
import com.salepilot.backend.security.UserPrincipal;
import com.salepilot.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of AuthService for handling authentication operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username is already taken");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email is already in use");
        }

        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .roles(new HashSet<>())
                .build();

        // Assign default role
        Role userRole = roleRepository.findByName(AppConstants.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        user.getRoles().add(userRole);

        // Save user
        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getUsername());

        // Create UserPrincipal and generate tokens
        UserPrincipal userPrincipal = UserPrincipal.create(savedUser);
        String accessToken = tokenProvider.generateAccessToken(userPrincipal);
        String refreshToken = tokenProvider.generateRefreshToken(userPrincipal);

        return buildAuthResponse(savedUser, accessToken, refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("User login attempt: {}", request.getUsernameOrEmail());

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()));

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        // Generate tokens
        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(userPrincipal);

        // Get user details
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        log.info("User logged in successfully: {}", user.getUsername());

        return buildAuthResponse(user, accessToken, refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Validate refresh token
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        // Check if token is actually a refresh token
        if (!tokenProvider.isRefreshToken(refreshToken)) {
            throw new UnauthorizedException("Token is not a refresh token");
        }

        // Get user from token
        Long userId = tokenProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        // Generate new tokens
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        String newAccessToken = tokenProvider.generateAccessToken(userPrincipal);
        String newRefreshToken = tokenProvider.generateRefreshToken(userPrincipal);

        log.info("Token refreshed for user: {}", user.getUsername());

        return buildAuthResponse(user, newAccessToken, newRefreshToken);
    }

    @Override
    public void logout(String token) {
        // In a stateless JWT implementation, logout is handled client-side
        // For more advanced implementations, you could:
        // - Add token to a blacklist in Redis
        // - Invalidate refresh tokens in database
        // - Clear any server-side session data
        log.info("User logout");
    }

    /**
     * Build authentication response
     */
    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        AuthResponse.UserResponse userResponse = AuthResponse.UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .roles(roles)
                .build();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(AppConstants.TOKEN_TYPE)
                .expiresIn(tokenProvider.getExpirationMs())
                .user(userResponse)
                .build();
    }
}
