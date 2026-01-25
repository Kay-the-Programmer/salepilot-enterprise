# Quick Start Guide - SalePilot Backend

This guide will help you get the SalePilot Backend up and running in less than 5 minutes.

## Prerequisites

- **Docker** and **Docker Compose** installed
- OR **Java 17+**, **Gradle**, and **PostgreSQL** installed

---

## Option 1: Docker Compose (Recommended) ⚡

This is the **fastest way** to get started!

### 1. Navigate to Project Directory

```bash
cd c:/Users/omen/Downloads/salepilot/java-backend/backend
```

### 2. Start All Services

```bash
docker-compose up -d
```

This will start:
- PostgreSQL database
- Backend application
- PgAdmin (database management UI)

### 3. Wait for Services to Start

```bash
# Check if services are running
docker-compose ps

# View logs
docker-compose logs -f backend
```

### 4. Access the Application

- **API Base URL**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health
- **PgAdmin**: http://localhost:5050
  - Email: admin@salepilot.com
  - Password: admin

### 5. Test the API

**Login with default admin user:**

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin",
    "password": "Admin@123"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "JWT",
    "expiresIn": 86400000,
    "user": {
      "id": 1,
      "username": "admin",
      "email": "admin@salepilot.com",
      "roles": ["ROLE_ADMIN"]
    }
  }
}
```

### 6. Use the Token

Copy the `accessToken` from the response and use it in subsequent requests:

```bash
curl http://localhost:8080/api/v1/users/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"
```

### Stop Services

```bash
docker-compose down
```

---

## Option 2: Local Development (Without Docker)

### 1. Set Up PostgreSQL Database

```bash
# Using psql
psql -U postgres
CREATE DATABASE salepilot_dev;
\q
```

### 2. Configure Environment

Create `.env` file:

```bash
cp .env.example .env
```

Edit `.env`:
```properties
DB_HOST=localhost
DB_PORT=5432
DB_NAME=salepilot_dev
DB_USERNAME=postgres
DB_PASSWORD=postgres
JWT_SECRET=your-256-bit-secret-your-256-bit-secret-your-256-bit-secret
```

### 3. Run the Application

```bash
# On Windows
gradlew.bat bootRun

# On Linux/Mac
./gradlew bootRun
```

The application will:
- Start on port 8080
- Run Flyway migrations automatically
- Create default roles and admin user

### 4. Verify It's Running

Visit: http://localhost:8080/swagger-ui.html

---

## Default Credentials

After first startup, you can login with:

- **Username**: `admin`
- **Email**: `admin@salepilot.com`
- **Password**: `Admin@123`

> ⚠️ **Important**: Change this password immediately in production!

---

## Available Roles

1. **ROLE_USER** - Standard user with basic permissions
2. **ROLE_MANAGER** - Manager with elevated permissions
3. **ROLE_ADMIN** - Full administrative access

---

## Common Commands

### Build the Project

```bash
# Windows
gradlew.bat build

# Linux/Mac
./gradlew build
```

### Run Tests

```bash
# Windows
gradlew.bat test

# Linux/Mac
./gradlew test
```

### Generate JAR

```bash
# Windows
gradlew.bat bootJar

# Linux/Mac
./gradlew bootJar
```

The JAR file will be in: `build/libs/backend-0.0.1-SNAPSHOT.jar`

### Run JAR

```bash
java -jar build/libs/backend-0.0.1-SNAPSHOT.jar
```

---

## Testing the API

### Using Swagger UI (Recommended for Beginners)

1. Go to http://localhost:8080/swagger-ui.html
2. Click on **"Authentication"** section
3. Try the **POST /api/v1/auth/login** endpoint
4. Click **"Try it out"**
5. Enter credentials and execute
6. Copy the `accessToken` from response
7. Click **"Authorize"** button at the top
8. Enter: `Bearer YOUR_TOKEN_HERE`
9. Now you can test all protected endpoints

### Using cURL

**Register a new user:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "SecurePass123!",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

**Get current user profile:**
```bash
curl http://localhost:8080/api/v1/users/me \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Get all users (Admin only):**
```bash
curl http://localhost:8080/api/v1/users \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

### Using Postman

1. Import the OpenAPI spec from: http://localhost:8080/v3/api-docs
2. Set up environment variable for `baseUrl`: http://localhost:8080
3. Add authorization header: `Bearer {{token}}`

---

## Monitoring & Health Checks

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

Response:
```json
{
  "status": "UP"
}
```

### Application Metrics
```bash
curl http://localhost:8080/actuator/metrics
```

### Prometheus Metrics
```bash
curl http://localhost:8080/actuator/prometheus
```

---

## Database Access

### Using PgAdmin (Docker Only)

1. Open http://localhost:5050
2. Login with:
   - Email: admin@salepilot.com
   - Password: admin
3. Add new server:
   - Host: postgres
   - Port: 5432
   - Database: salepilot_dev
   - Username: postgres
   - Password: postgres

### Using psql

```bash
psql -h localhost -U postgres -d salepilot_dev
```

Common queries:
```sql
-- View all users
SELECT * FROM users;

-- View all roles
SELECT * FROM roles;

-- View user roles
SELECT u.username, r.name as role
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id;
```

---

## Troubleshooting

### Port Already in Use

If port 8080 is already in use, change it in `application-dev.yml`:

```yaml
server:
  port: 8081
```

### Database Connection Failed

1. Ensure PostgreSQL is running
2. Check credentials in `.env` or `application-dev.yml`
3. Verify database exists: `psql -U postgres -l`

### Build Errors

```bash
# Clean and rebuild
gradlew.bat clean build --refresh-dependencies
```

### Docker Issues

```bash
# Stop and remove all containers
docker-compose down -v

# Rebuild and start
docker-compose up -d --build
```

---

## Next Steps

1. ✅ **Explore the API** - Use Swagger UI to test all endpoints
2. ✅ **Read the Documentation** - Check `README.md` and `API_DOCUMENTATION.md`
3. ✅ **Create Custom Entities** - Add your business domain models
4. ✅ **Integrate with Frontend** - Connect your React/Angular/Vue app
5. ✅ **Deploy** - Use Docker for easy deployment

---

## Important Files

- **Configuration**: `src/main/resources/application*.yml`
- **Database Migrations**: `src/main/resources/db/migration/`
- **Main Application**: `src/main/java/com/salepilot/backend/SalepilotApplication.java`
- **API Documentation**: Available at `/swagger-ui.html` when running

---

## Support

For detailed documentation, see:
- [README.md](./README.md) - Complete setup guide
- [API_DOCUMENTATION.md](./API_DOCUMENTATION.md) - API reference

---

**You're all set! 🎉**

The backend is now running and ready to handle requests!
