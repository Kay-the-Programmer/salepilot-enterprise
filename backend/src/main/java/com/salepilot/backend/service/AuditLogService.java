package com.salepilot.backend.service;

import com.salepilot.backend.context.TenantContext;
import com.salepilot.backend.entity.AuditLog;
import com.salepilot.backend.entity.User;
import com.salepilot.backend.repository.AuditLogRepository;
import com.salepilot.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Service layer for Audit Logs.
 * Handles recording security events.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    /**
     * Log an action
     */
    public void logAction(String userId, String action, String details) {
        // Can be async in production
        try {
            User user = userRepository.findById(Long.parseLong(userId)).orElse(null);

            if (user != null) {
                AuditLog log = AuditLog.builder()
                        .timestamp(Instant.now())
                        .user(user)
                        .userName(user.getFirstName() + " " + user.getLastName())
                        .action(action)
                        .details(details)
                        .build();

                // Store ID is handled by Aspect/TenantContext, but here we might need to be
                // careful
                // if this is called from a tailored context.
                // Assuming TenantContext is set.

                auditLogRepository.save(log);
            }
        } catch (Exception e) {
            // Audit logging should not fail the main transaction usually
            // but for high security it should.
            // keeping silent for now to avoid disrupting flow if user not found (e.g.
            // system action)
            System.err.println("Failed to log audit: " + e.getMessage());
        }
    }

    /**
     * Get logs
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> getLogs(Pageable pageable) {
        String storeId = TenantContext.getCurrentTenant();
        return auditLogRepository.findByStoreIdOrderByTimestampDesc(storeId, pageable);
    }
}
