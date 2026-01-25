# Development Guide

## Project Structure Overview

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/salepilot/backend/
│   │   │   ├── config/              # Spring configuration classes
│   │   │   │   ├── AsyncConfig.java
│   │   │   │   ├── AuditingConfig.java
│   │   │   │   ├── CacheConfig.java
│   │   │   │   ├── CorsConfig.java
│   │   │   │   ├── OpenApiConfig.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── constant/            # Application constants
│   │   │   │   └── AppConstants.java
│   │   │   ├── controller/          # REST API controllers
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── HealthController.java
│   │   │   │   └── UserController.java
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   │   ├── request/
│   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   ├── RefreshTokenRequest.java
│   │   │   │   │   └── RegisterRequest.java
│   │   │   │   └── response/
│   │   │   │       ├── ApiResponse.java
│   │   │   │       ├── AuthResponse.java
│   │   │   │       ├── ErrorResponse.java
│   │   │   │       └── PageResponse.java
│   │   │   ├── entity/              # JPA entities
│   │   │   │   ├── BaseEntity.java
│   │   │   │   ├── Permission.java
│   │   │   │   ├── Role.java
│   │   │   │   └── User.java
│   │   │   ├── exception/           # Custom exceptions
│   │   │   │   ├── BadRequestException.java
│   │   │   │   ├── ConflictException.java
│   │   │   │   ├── ForbiddenException.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   └── UnauthorizedException.java
│   │   │   ├── repository/          # Data access layer
│   │   │   │   ├── PermissionRepository.java
│   │   │   │   ├── RoleRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   ├── security/            # Security components
│   │   │   │   ├── JwtAuthenticationEntryPoint.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   ├── UserDetailsServiceImpl.java
│   │   │   │   └── UserPrincipal.java
│   │   │   ├── service/             # Business logic
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── UserService.java
│   │   │   │   └── impl/
│   │   │   │       ├── AuthServiceImpl.java
│   │   │   │       └── UserServiceImpl.java
│   │   │   ├── util/                # Utility classes
│   │   │   │   ├── DateUtils.java
│   │   │   │   ├── PageUtils.java
│   │   │   │   └── StringUtils.java
│   │   │   └── SalepilotApplication.java
│   │   └── resources/
│   │       ├── db/migration/        # Flyway migrations
│   │       │   ├── V1__Initial_Schema.sql
│   │       │   └── V2__Seed_Initial_Data.sql
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       └── application-prod.yml
│   └── test/
│       ├── java/com/salepilot/backend/
│       │   └── SalepilotApplicationTests.java
│       └── resources/
│           └── application-test.yml
├── build.gradle
├── Dockerfile
├── docker-compose.yml
├── .env.example
├── .gitignore
├── README.md
├── API_DOCUMENTATION.md
└── QUICK_START.md
```

## Adding New Features

### 1. Creating a New Entity

**Example: Product Entity**

```java
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "price", nullable = false)
    private BigDecimal price;
    
    @Column(name = "stock_quantity")
    private Integer stockQuantity;
}
```

### 2. Creating a Repository

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findByName(String name);
    
    @Query("SELECT p FROM Product p WHERE p.deleted = false")
    Page<Product> findAllActive(Pageable pageable);
}
```

### 3. Creating a Service

**Interface:**
```java
public interface ProductService {
    Product createProduct(Product product);
    Optional<Product> getProductById(Long id);
    Page<Product> getAllProducts(Pageable pageable);
    Product updateProduct(Long id, Product product);
    void deleteProduct(Long id);
}
```

**Implementation:**
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    
    @Override
    @Transactional
    public Product createProduct(Product product) {
        log.info("Creating product: {}", product.getName());
        return productRepository.save(product);
    }
    
    // ... other methods
}
```

### 4. Creating a Controller

```java
@RestController
@RequestMapping(AppConstants.API_BASE_PATH + "/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Products", description = "Product management API")
@SecurityRequirement(name = "Bearer Authentication")
public class ProductController {
    
    private final ProductService productService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Product>> createProduct(
            @Valid @RequestBody Product product) {
        Product created = productService.createProduct(product);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created", created));
    }
    
    // ... other endpoints
}
```

### 5. Creating a Database Migration

Create file: `src/main/resources/db/migration/V3__Create_Products_Table.sql`

```sql
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version BIGINT DEFAULT 0,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_product_name ON products(name);
CREATE INDEX idx_product_deleted ON products(deleted);
```

## Best Practices

### 1. Entity Design
- Always extend `BaseEntity` for audit fields
- Use `@Builder` for entity construction
- Add appropriate indexes in migrations
- Use soft delete instead of hard delete

### 2. Service Layer
- Use `@Transactional` for write operations
- Add `@Transactional(readOnly = true)` for read operations
- Use `@Cacheable` for frequently accessed data
- Log important operations

### 3. Controller Design
- Use proper HTTP methods (GET, POST, PUT, DELETE)
- Return appropriate HTTP status codes
- Use `@PreAuthorize` for role-based access
- Add OpenAPI annotations for documentation

### 4. Error Handling
- Throw specific exceptions (not generic Exception)
- Let GlobalExceptionHandler catch and format errors
- Provide meaningful error messages

### 5. Testing
- Write unit tests for service layer
- Write integration tests for repositories
- Test API endpoints with MockMvc
- Aim for 70%+ code coverage

## Security Considerations

### 1. Password Storage
- Never store passwords in plain text
- Use BCrypt with strength 12+
- Validate password complexity

### 2. JWT Tokens
- Keep token secret secure
- Use strong signing algorithm (HS512)
- Set appropriate expiration times
- Implement token refresh mechanism

### 3. API Security
- Validate all input data
- Use HTTPS in production
- Implement rate limiting
- Log security events

### 4. Database Security
- Use parameterized queries (JPA handles this)
- Never expose sensitive data in responses
- Encrypt sensitive columns if needed

## Performance Tips

### 1. Caching
```java
@Cacheable(value = "products", key = "#id")
public Optional<Product> getProductById(Long id) {
    return productRepository.findById(id);
}

@CacheEvict(value = "products", key = "#id")
public void deleteProduct(Long id) {
    // ...
}
```

### 2. Database Optimization
- Use indexes on frequently queried columns
- Use pagination for large datasets
- Avoid N+1 query problems
- Use `@Query` for complex queries

### 3. Connection Pooling
Already configured in `application.yml`:
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
```

## Deployment

### Local Development
```bash
./gradlew bootRun
```

### Production JAR
```bash
./gradlew bootJar
java -jar build/libs/backend-0.0.1-SNAPSHOT.jar
```

### Docker
```bash
docker build -t salepilot-backend .
docker run -p 8080:8080 salepilot-backend
```

### Docker Compose
```bash
docker-compose up -d
```

## Monitoring

### Actuator Endpoints
- `/actuator/health` - Health status
- `/actuator/metrics` - Application metrics
- `/actuator/prometheus` - Prometheus metrics
- `/actuator/info` - Application info

### Logging
Configure log levels in `application.yml`:
```yaml
logging:
  level:
    root: INFO
    com.salepilot: DEBUG
```

## Common Issues & Solutions

### Issue: Port 8080 already in use
**Solution**: Change port in `application.yml` or stop the process using port 8080

### Issue: Database connection refused
**Solution**: Ensure PostgreSQL is running and credentials are correct

### Issue: JWT token expired
**Solution**: Use refresh token to get new access token

### Issue: Build fails
**Solution**: Run `./gradlew clean build --refresh-dependencies`

## Contributing

1. Create a feature branch
2. Make your changes
3. Write tests
4. Ensure all tests pass
5. Update documentation
6. Submit pull request

## Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security](https://docs.spring.io/spring-security/reference/index.html)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Flyway](https://flywaydb.org/documentation/)
