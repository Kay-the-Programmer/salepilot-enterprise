package com.salepilot.backend.service;

import com.salepilot.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service interface for user operations.
 */
public interface UserService {

    /**
     * Get user by ID
     */
    Optional<User> getUserById(Long id);

    /**
     * Get user by username
     */
    Optional<User> getUserByUsername(String username);

    /**
     * Get user by email
     */
    Optional<User> getUserByEmail(String email);

    /**
     * Get all active users (paginated)
     */
    Page<User> getAllActiveUsers(Pageable pageable);

    /**
     * Search users
     */
    Page<User> searchUsers(String search, Pageable pageable);

    /**
     * Update user
     */
    User updateUser(Long id, User user);

    /**
     * Delete user (soft delete)
     */
    void deleteUser(Long id);

    /**
     * Check if user exists by username
     */
    boolean existsByUsername(String username);

    /**
     * Check if user exists by email
     */
    boolean existsByEmail(String email);
}
