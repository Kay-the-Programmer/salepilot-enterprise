package com.salepilot.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main application class for SalePilot Backend Microservice.
 * 
 * This enterprise-grade Spring Boot application provides:
 * - RESTful API endpoints
 * - JWT and OAuth2 authentication
 * - PostgreSQL database integration
 * - Caching with Caffeine
 * - Async processing capabilities
 * - JPA auditing for entity tracking
 * 
 * @author SalePilot Team
 * @version 0.0.1-SNAPSHOT
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
public class SalepilotApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalepilotApplication.class, args);
	}

}
