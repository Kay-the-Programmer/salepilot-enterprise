# API Documentation

## Authentication Endpoints

### Register User

**POST** `/api/v1/auth/register`

Register a new user account.

**Request Body:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePassword123!",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890"
}
```

**Response:** `201 Created`
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "JWT",
    "expiresIn": 86400000,
    "user": {
      "id": 1,
      "username": "john_doe",
      "email": "john@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "fullName": "John Doe",
      "avatarUrl": null,
      "roles": ["ROLE_USER"]
    }
  }
}
```

---

### Login

**POST** `/api/v1/auth/login`

Authenticate user and receive JWT tokens.

**Request Body:**
```json
{
  "usernameOrEmail": "john_doe",
  "password": "SecurePassword123!"
}
```

**Response:** `200 OK`
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
      "username": "john_doe",
      "email": "john@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "fullName": "John Doe",
      "avatarUrl": null,
      "roles": ["ROLE_USER"]
    }
  }
}
```

---

### Refresh Token

**POST** `/api/v1/auth/refresh`

Get a new access token using refresh token.

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9..."
}
```

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Token refreshed successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "JWT",
    "expiresIn": 86400000,
    "user": {
      "id": 1,
      "username": "john_doe",
      "email": "john@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "fullName": "John Doe",
      "avatarUrl": null,
      "roles": ["ROLE_USER"]
    }
  }
}
```

---

### Logout

**POST** `/api/v1/auth/logout`

Logout user (token handled client-side).

**Headers:**
```
Authorization: Bearer <access_token>
```

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Logout successful",
  "data": null
}
```

---

## User Management Endpoints

### Get Current User Profile

**GET** `/api/v1/users/me`

Get authenticated user's profile.

**Headers:**
```
Authorization: Bearer <access_token>
```

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+1234567890",
    "enabled": true,
    "accountNonExpired": true,
    "accountNonLocked": true,
    "credentialsNonExpired": true,
    "roles": [
      {
        "id": 1,
        "name": "ROLE_USER",
        "description": "Standard user role"
      }
    ],
    "createdAt": "2026-01-25T20:00:00",
    "updatedAt": "2026-01-25T20:00:00"
  }
}
```

---

### Get All Users (Admin/Manager Only)

**GET** `/api/v1/users`

Get paginated list of all users.

**Query Parameters:**
- `page` (default: 0) - Page number
- `size` (default: 20) - Page size
- `sortBy` (default: id) - Sort field
- `sortDir` (default: DESC) - Sort direction (ASC/DESC)

**Headers:**
```
Authorization: Bearer <access_token>
```

**Required Role:** `ROLE_ADMIN` or `ROLE_MANAGER`

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "username": "john_doe",
        "email": "john@example.com",
        "firstName": "John",
        "lastName": "Doe"
      }
    ],
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 1,
    "totalPages": 1,
    "last": true,
    "first": true,
    "numberOfElements": 1
  }
}
```

---

### Search Users (Admin/Manager Only)

**GET** `/api/v1/users/search`

Search users by name, email, or username.

**Query Parameters:**
- `q` (required) - Search query
- `page` (default: 0) - Page number
- `size` (default: 20) - Page size

**Headers:**
```
Authorization: Bearer <access_token>
```

**Required Role:** `ROLE_ADMIN` or `ROLE_MANAGER`

**Response:** `200 OK` (same format as Get All Users)

---

### Update Current User Profile

**PUT** `/api/v1/users/me`

Update authenticated user's profile.

**Headers:**
```
Authorization: Bearer <access_token>
```

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Updated",
  "phone": "+1234567890",
  "avatarUrl": "https://example.com/avatar.jpg"
}
```

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Profile updated successfully",
  "data": {
    "id": 1,
    "username": "john_doe",
    "firstName": "John",
    "lastName": "Updated",
    "phone": "+1234567890",
    "avatarUrl": "https://example.com/avatar.jpg"
  }
}
```

---

### Delete User (Admin Only)

**DELETE** `/api/v1/users/{id}`

Soft delete a user by ID.

**Headers:**
```
Authorization: Bearer <access_token>
```

**Required Role:** `ROLE_ADMIN`

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "User deleted successfully",
  "data": null
}
```

---

## Error Responses

All endpoints may return the following error responses:

### 400 Bad Request
```json
{
  "timestamp": "2026-01-25T20:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid input data",
  "path": "/api/v1/auth/register",
  "errors": [
    {
      "field": "email",
      "message": "Email is required",
      "rejectedValue": null
    }
  ]
}
```

### 401 Unauthorized
```json
{
  "timestamp": "2026-01-25T20:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid username or password",
  "path": "/api/v1/auth/login"
}
```

### 403 Forbidden
```json
{
  "timestamp": "2026-01-25T20:00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied",
  "path": "/api/v1/users"
}
```

### 404 Not Found
```json
{
  "timestamp": "2026-01-25T20:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: '999'",
  "path": "/api/v1/users/999"
}
```

### 409 Conflict
```json
{
  "timestamp": "2026-01-25T20:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "Username is already taken",
  "path": "/api/v1/auth/register"
}
```

### 500 Internal Server Error
```json
{
  "timestamp": "2026-01-25T20:00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred. Please try again later.",
  "path": "/api/v1/users"
}
```

---

## Authentication Flow

1. **Register** new user account or **Login** with existing credentials
2. Receive `accessToken` and `refreshToken`
3. Include `accessToken` in Authorization header for protected endpoints:
   ```
   Authorization: Bearer <access_token>
   ```
4. When `accessToken` expires, use `refreshToken` to get new tokens
5. **Logout** when done (clear tokens client-side)

---

## Rate Limiting

API requests are rate-limited to 100 requests per second per IP address.

---

## Pagination

All list endpoints support pagination with the following query parameters:

- `page` - Page number (0-indexed)
- `size` - Number of items per page (max: 100)
- `sortBy` - Field to sort by
- `sortDir` - Sort direction (ASC or DESC)

---

## Timestamps

All timestamps are in ISO 8601 format (UTC): `yyyy-MM-dd'T'HH:mm:ss`
