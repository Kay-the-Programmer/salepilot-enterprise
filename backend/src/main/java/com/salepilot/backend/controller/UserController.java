package com.salepilot.backend.controller;

import com.salepilot.backend.constant.AppConstants;
import com.salepilot.backend.dto.response.ApiResponse;
import com.salepilot.backend.dto.response.PageResponse;
import com.salepilot.backend.entity.User;
import com.salepilot.backend.exception.ResourceNotFoundException;
import com.salepilot.backend.security.UserPrincipal;
import com.salepilot.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user management endpoints.
 */
@RestController
@RequestMapping(AppConstants.API_BASE_PATH + "/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "User management API endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get current user profile")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("GET /users/me - Getting current user profile");

        User user = userService.getUserById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));

        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @Operation(summary = "Get user by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        log.info("GET /users/{} - Getting user by ID", id);

        User user = userService.getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @Operation(summary = "Get all users (paginated)")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<PageResponse<User>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {

        log.info("GET /users - Getting all users, page: {}, size: {}", page, size);

        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> userPage = userService.getAllActiveUsers(pageable);

        PageResponse<User> pageResponse = PageResponse.<User>builder()
                .content(userPage.getContent())
                .pageNumber(userPage.getNumber())
                .pageSize(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .last(userPage.isLast())
                .first(userPage.isFirst())
                .numberOfElements(userPage.getNumberOfElements())
                .build();

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @Operation(summary = "Search users")
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<PageResponse<User>>> searchUsers(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {

        log.info("GET /users/search - Searching users with query: {}", q);

        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userService.searchUsers(q, pageable);

        PageResponse<User> pageResponse = PageResponse.<User>builder()
                .content(userPage.getContent())
                .pageNumber(userPage.getNumber())
                .pageSize(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .last(userPage.isLast())
                .first(userPage.isFirst())
                .numberOfElements(userPage.getNumberOfElements())
                .build();

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @Operation(summary = "Update current user profile")
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<User>> updateCurrentUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody User user) {

        log.info("PUT /users/me - Updating current user profile");

        User updatedUser = userService.updateUser(userPrincipal.getId(), user);

        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updatedUser));
    }

    @Operation(summary = "Update user by ID")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @PathVariable Long id,
            @RequestBody User user) {

        log.info("PUT /users/{} - Updating user", id);

        User updatedUser = userService.updateUser(id, user);

        return ResponseEntity.ok(ApiResponse.success("User updated successfully", updatedUser));
    }

    @Operation(summary = "Delete user by ID")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        log.info("DELETE /users/{} - Deleting user", id);

        userService.deleteUser(id);

        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }
}
