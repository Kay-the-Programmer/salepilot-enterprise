package com.salepilot.backend.repository;

import com.salepilot.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by username or email
     */
    @Query("SELECT u FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

    /**
     * Find user by OAuth provider and ID
     */
    Optional<User> findByOauthProviderAndOauthId(String oauthProvider, String oauthId);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Find all non-deleted users
     */
    @Query("SELECT u FROM User u WHERE u.deleted = false")
    Page<User> findAllActive(Pageable pageable);

    /**
     * Find users by role name
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.deleted = false")
    Page<User> findByRoleName(@Param("roleName") String roleName, Pageable pageable);

    /**
     * Search users by name or email
     */
    @Query("SELECT u FROM User u WHERE u.deleted = false AND " +
            "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> searchUsers(@Param("search") String search, Pageable pageable);

    /**
     * Find user by verification token
     */
    Optional<User> findByVerificationToken(String verificationToken);

    /**
     * Find user by reset password token
     */
    Optional<User> findByResetPasswordToken(String resetPasswordToken);
}
