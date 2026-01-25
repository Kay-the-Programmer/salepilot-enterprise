package com.salepilot.backend.security;

import com.salepilot.backend.context.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Servlet filter that extracts the tenant (store) ID from the authenticated
 * user
 * and sets it in the TenantContext for the duration of the request.
 * 
 * The tenant ID is expected to be available in the UserDetails implementation.
 */
@Component
public class TenantFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(TenantFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()
                    && authentication.getPrincipal() instanceof UserPrincipal) {

                UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
                String storeId = userDetails.getCurrentStoreId();

                if (storeId != null && !storeId.trim().isEmpty()) {
                    TenantContext.setCurrentTenant(storeId);
                    logger.debug("Tenant context set for store: {} (user: {})",
                            storeId, userDetails.getUsername());
                } else {
                    logger.debug("No store ID found for user: {}", userDetails.getUsername());
                }
            }

            filterChain.doFilter(request, response);

        } finally {
            // Always clear tenant context after request to prevent memory leaks
            TenantContext.clear();
        }
    }
}
