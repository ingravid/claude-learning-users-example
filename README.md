# User Management REST API

A Spring Boot REST application for managing users with CRUD operations, built with Java 21 and H2 in-memory database.

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Building the Project](#building-the-project)
- [Running the Application](#running-the-application)
- [Quick Start](#quick-start)
- [API Summary](#api-summary)
- [Interactive API Documentation](#interactive-api-documentation)
- [API Endpoints](#api-endpoints)
  - [Register](#register)
  - [Login](#login)
  - [Get All Users](#get-all-users)
  - [Create User](#create-user)
  - [Get User by ID](#get-user-by-id)
  - [Update User](#update-user)
  - [Delete User](#delete-user)
  - [Get Greeting](#get-greeting)
- [Error Response Format](#error-response-format)
- [H2 Database Console](#h2-database-console)
- [Testing](#testing)
- [Example Usage with curl](#example-usage-with-curl)
- [Design Decisions](#design-decisions)
- [Project Roadmap & TODO List](#-project-roadmap--todo-list)

## Features

- Complete CRUD operations (Create, Read, Update, Delete)
- Get all users with pagination support
- JWT authentication (HS256, 24-hour expiry) with register/login endpoints
- Password hashing with BCrypt
- Input validation with Bean Validation
- Global exception handling with consistent error responses
- Interactive OpenAPI/Swagger documentation
- H2 in-memory database with console access
- Java Records for immutable DTOs
- RESTful API design
- Comprehensive test coverage (38 tests)

## Tech Stack

- **Java**: 21
- **Spring Boot**: 3.2.5
- **Build Tool**: Maven
- **Database**: H2 (in-memory)
- **ORM**: Spring Data JPA
- **Validation**: Jakarta Bean Validation
- **Security**: Spring Security + JWT (jjwt 0.12.6)

## Prerequisites

- Java 21 or higher
- Maven 3.6+ (or use the included Maven wrapper)

## Project Structure

```
src/main/java/com/learning/usermanagement/
├── UserManagementApplication.java    # Main application class
├── config/                            # Spring configuration (Security, OpenAPI)
├── controller/                        # REST API endpoints
├── service/                           # Business logic layer
├── security/                          # JWT filter, entry point, UserDetailsService
├── repository/                        # Data access layer
├── model/                             # JPA entities and enums
├── dto/                               # Data Transfer Objects (Records)
└── exception/                         # Custom exceptions and handlers
```

## Building the Project

```bash
# Using Maven wrapper (recommended)
./mvnw clean package

# Using installed Maven
mvn clean package
```

## Running the Application

```bash
# Using Maven wrapper
./mvnw spring-boot:run

# Using installed Maven
mvn spring-boot:run

# Running the JAR directly
java -jar target/user-management-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

## Quick Start

```bash
# 1. Clone and navigate to project
cd prompting_to_project

# 2. Build the project
./mvnw clean package

# 3. Run the application
./mvnw spring-boot:run

# 4. Test the API (in a new terminal)

# Register an account and receive a JWT token
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name": "Alice", "email": "alice@example.com", "password": "secret123"}'
# Response: {"token": "eyJhbGci..."}

# Use the token to call protected endpoints
TOKEN="<paste token here>"

# Create a user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name": "Bob", "email": "bob@example.com"}'

# Get all users
curl http://localhost:8080/api/users \
  -H "Authorization: Bearer $TOKEN"

# Access H2 console
# Open browser: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:userdb
# Username: sa
# Password: (leave empty)
```

## API Summary

| Method | Endpoint | Description | Request Body | Success Response |
|--------|----------|-------------|--------------|------------------|
| POST | `/api/auth/register` | Register a new account | `{"name": "...", "email": "...", "password": "..."}` | 201 Created |
| POST | `/api/auth/login` | Login and receive JWT | `{"email": "...", "password": "..."}` | 200 OK |
| GET | `/api/users` | Get all users (paginated) | - | 200 OK |
| POST | `/api/users` | Create a new user | `{"name": "...", "email": "..."}` | 201 Created |
| GET | `/api/users/{id}` | Get user by ID | - | 200 OK |
| PUT | `/api/users/{id}` | Update user | `{"name": "...", "email": "..."}` | 200 OK |
| DELETE | `/api/users/{id}` | Delete user | - | 204 No Content |
| GET | `/api/greetings` | Get greeting message | - | 200 OK |

**Common Error Responses:**
- `400 Bad Request` - Validation failed
- `401 Unauthorized` - Invalid credentials
- `404 Not Found` - User does not exist
- `409 Conflict` - Email already exists
- `500 Internal Server Error` - Server error

## Interactive API Documentation

This project includes interactive API documentation powered by OpenAPI/Swagger.

**Access Swagger UI**: http://localhost:8080/swagger-ui.html

**OpenAPI JSON**: http://localhost:8080/v3/api-docs

The Swagger UI provides:
- Complete API endpoint documentation
- Request/response schemas with example values
- Try-it-out functionality for testing endpoints directly
- All HTTP status codes documented for each endpoint
- Interactive testing without external tools like curl or Postman

## API Endpoints

### Register

**POST** `/api/auth/register`

Creates a new account and returns a JWT token.

**Request Body:**
```json
{
  "name": "Alice",
  "email": "alice@example.com",
  "password": "secret123"
}
```

**Success Response:** `201 Created`
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Error Responses:**
- `400 Bad Request` - Validation failed (blank fields or invalid email)
- `409 Conflict` - Email already in use

---

### Login

**POST** `/api/auth/login`

Authenticates a user and returns a JWT token.

**Request Body:**
```json
{
  "email": "alice@example.com",
  "password": "secret123"
}
```

**Success Response:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Error Responses:**
- `400 Bad Request` - Validation failed
- `401 Unauthorized` - Invalid email or password

---

### Get All Users

**GET** `/api/users?page=0&size=10`

Retrieves a paginated list of all users.

**Query Parameters:**
- `page` (optional, default: 0) - Page number (zero-indexed)
- `size` (optional, default: 10) - Number of users per page

**Success Response:** `200 OK`
```json
{
  "content": [
    {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com"
    },
    {
      "id": 2,
      "name": "Jane Smith",
      "email": "jane@example.com"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 2,
  "totalPages": 1,
  "last": true,
  "first": true,
  "empty": false
}
```

### Create User

**POST** `/api/users`

Creates a new user with the provided name and email.

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com"
}
```

**Success Response:** `201 Created`
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com"
}
```

**Error Responses:**
- `400 Bad Request` - Validation failed (blank name/email or invalid email format)
- `409 Conflict` - Email already exists

### Get User by ID

**GET** `/api/users/{id}`

Retrieves a user by their ID.

**Success Response:** `200 OK`
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com"
}
```

**Error Response:**
- `404 Not Found` - User does not exist

### Update User

**PUT** `/api/users/{id}`

Updates an existing user's name and/or email.

**Request Body:**
```json
{
  "name": "John Updated",
  "email": "john.updated@example.com"
}
```

**Success Response:** `200 OK`
```json
{
  "id": 1,
  "name": "John Updated",
  "email": "john.updated@example.com"
}
```

**Error Responses:**
- `400 Bad Request` - Validation failed (blank name/email or invalid email format)
- `404 Not Found` - User does not exist
- `409 Conflict` - Email already exists (when changing to another user's email)

**Note:** Users can keep their existing email when updating their name.

### Delete User

**DELETE** `/api/users/{id}`

Deletes a user by their ID.

**Success Response:** `204 No Content`

**Error Response:**
- `404 Not Found` - User does not exist

### Get Greeting

**GET** `/api/greetings`

Returns a simple greeting message.

**Success Response:** `200 OK`
```
Hello user
```

## Error Response Format

All errors return a consistent JSON structure:

```json
{
  "timestamp": "2026-03-23T18:30:00.123",
  "status": 404,
  "message": "User not found with id: 999"
}
```

## H2 Database Console

The H2 console is available for database inspection during development.

**URL:** `http://localhost:8080/h2-console`

**Connection Settings:**
- **JDBC URL:** `jdbc:h2:mem:userdb`
- **Username:** `sa`
- **Password:** (leave empty)

## Testing

The project has comprehensive test coverage across all layers:

- **38 total tests**
- Unit tests (UserServiceTest) - 14 tests
- Integration tests (UserControllerIntegrationTest) - 10 tests
- Integration tests (GreetingControllerIntegrationTest) - 1 test
- Repository tests (UserRepositoryTest) - 7 tests
- Exception handler tests (GlobalExceptionHandlerTest) - 5 tests
- Application context test - 1 test

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=UserServiceTest

# Run tests with coverage
./mvnw verify

# Clean build with tests
./mvnw clean package
```

## Example Usage with curl

```bash
# Register a new account
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name": "Alice", "email": "alice@example.com", "password": "secret123"}'

# Login and receive a JWT token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "alice@example.com", "password": "secret123"}'

# Login with wrong password (returns 401)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "alice@example.com", "password": "wrong"}'

# Store the token for subsequent requests
TOKEN="eyJhbGciOiJIUzI1NiJ9..."   # paste token from register/login response

# Get all users (with pagination) — requires auth
curl "http://localhost:8080/api/users?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"

# Get all users (default pagination) — requires auth
curl http://localhost:8080/api/users \
  -H "Authorization: Bearer $TOKEN"

# Create a user — requires auth
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name": "Jane Doe", "email": "jane@example.com"}'

# Get a user by ID — requires auth
curl http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer $TOKEN"

# Update a user — requires auth
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name": "Jane Updated", "email": "jane.updated@example.com"}'

# Update user name only (keep same email) — requires auth
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name": "Jane Smith", "email": "jane@example.com"}'

# Delete a user — requires auth
curl -X DELETE http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer $TOKEN"

# Error examples:

# Access protected endpoint without token (returns 401)
curl http://localhost:8080/api/users

# Create a user with validation error
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name": "", "email": "invalid"}'

# Create a user with duplicate email
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name": "John Smith", "email": "jane@example.com"}'

# Try to get non-existent user
curl http://localhost:8080/api/users/999 \
  -H "Authorization: Bearer $TOKEN"

# Try to update non-existent user
curl -X PUT http://localhost:8080/api/users/999 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name": "Ghost", "email": "ghost@example.com"}'

# Try to delete non-existent user
curl -X DELETE http://localhost:8080/api/users/999 \
  -H "Authorization: Bearer $TOKEN"

# Get greeting message (public — no auth needed)
curl http://localhost:8080/api/greetings
```

## Design Decisions

- **Java Records for DTOs**: Leverages Java 21 for concise, immutable data carriers
- **Manual Constructor Injection**: Explicit dependency injection without framework-specific annotations
- **Layered Architecture**: Clear separation between controller, service, and repository layers
- **Global Exception Handling**: Centralized error handling using `@RestControllerAdvice`
- **Bean Validation**: Declarative validation on record components
- **Pagination Support**: Uses Spring Data's `Page` and `Pageable` for efficient large dataset handling
- **Test-Driven Development**: All features implemented using TDD methodology (Red-Green-Refactor)
- **Transactional Updates**: Update and delete operations use `@Transactional` for data consistency

## 🎯 Project Roadmap & TODO List

This section tracks planned improvements and features for this AI-assisted learning project. The format combines Emacs org-mode headers with Markdown for clear status tracking.

### ***** DONE 1. Testing (Critical Gap)

Currently comprehensive tests are implemented:

**Unit Tests:**
- ✅ UserServiceTest - Mocks repository, tests business logic:
  - Creating users with valid data
  - Duplicate email detection
  - User not found scenarios
- ✅ UserRepositoryTest - Tests Spring Data JPA queries:
  - existsByEmail() behavior
  - findByEmail() optional handling
  - Unique constraint violations

**Integration Tests:**
- ✅ UserControllerIntegrationTest - Uses @SpringBootTest + MockMvc:
  - Full request/response cycle testing
  - All HTTP status codes (201, 200, 400, 404, 409)
  - JSON serialization/deserialization
  - Validation error message format

**Test Coverage:** 38 tests across all layers

---

### ***** IN-PROGRESS 2. Security

**Spring Security with JWT:**
- ✅ Bearer token generation (HS256, jjwt 0.12.6)
- ✅ Password hashing (BCrypt)
- ✅ User.password field added to entity
- ✅ JWT validation on incoming requests (JwtAuthenticationFilter)
- ✅ User roles (ADMIN, USER) — Role enum, stored in DB
- ✅ Endpoint protection: all /api/users/** require valid JWT

**Security Configuration:**
- ✅ POST /api/auth/login - Generate JWT
- ✅ POST /api/auth/register - Create account
- ✅ /api/users/** protected by JwtAuthenticationFilter
- [ ] Role-based authorization with @PreAuthorize (e.g., only ADMIN can create/delete users)

---

### ***** DONE 3. API Completeness

**Completed CRUD Operations:**
- ✅ POST /api/users - Create user
- ✅ GET /api/users/{id} - Get user by ID
- ✅ PUT /api/users/{id} - Update user
- ✅ DELETE /api/users/{id} - Delete user
- ✅ GET /api/users - List all users with pagination

**Future Enhancements:**
- [ ] GET /api/users?email={email} - Search by email
- [ ] Advanced filtering and sorting options

---

### ***** TODO 4. Data Validation Enhancement

Current validation is basic:

**Improvements:**
- Email format validation beyond @Email (domain whitelist?)
- Name length constraints (@Size(min=2, max=100))
- Custom validators for business rules
- Sanitization to prevent XSS

---

### ***** DONE 5. API Documentation

**Completed:**
- ✅ Added springdoc-openapi-starter-webmvc-ui dependency
- ✅ Annotated controllers with @Operation, @ApiResponse
- ✅ Auto-generated interactive API docs at /swagger-ui.html
- ✅ All endpoints documented with examples and schemas
- ✅ OpenAPI 3.0 JSON spec available at /v3/api-docs

---

### ***** TODO 6. Observability & Monitoring

No logging or metrics currently:

**Spring Boot Actuator:**
- Health checks (/actuator/health)
- Metrics (/actuator/metrics)
- Structured logging (SLF4J with proper levels)
- Request/response logging interceptor
- Correlation IDs for tracing

---

### ***** TODO 7. Database Improvements

**Migration from H2:**
- PostgreSQL or MySQL for production
- Flyway or Liquibase for schema versioning
- Database migration scripts in src/main/resources/db/migration/

**Entity Enhancements:**
- Audit fields (createdAt, updatedAt, createdBy)
- Use @CreationTimestamp and @UpdateTimestamp
- Soft delete with deletedAt field

---

### ***** TODO 8. Error Handling Refinement

**Current Issues:**
- Generic 500 errors expose no details
- Limited request validation error details

**Improvements:**
- Return field-level validation errors (map of field → error)
- Add error codes for client handling
- Internationalization (i18n) for error messages
- Custom error response with ErrorResponse enhancement

---

### ***** TODO 9. Configuration Management

**Profiles:**
- application-dev.yml - Development settings
- application-prod.yml - Production settings
- application-test.yml - Test settings
- Externalize sensitive config (database credentials)

---

### ***** TODO 10. Performance & Scalability

**Caching:**
- Add @Cacheable on getUserById()
- Redis or Caffeine cache
- Cache eviction on updates

**Connection Pooling:**
- HikariCP configuration tuning
- Connection pool sizing

**Pagination:**
- ✅ Already implemented Page<UserResponseDto> for list endpoints
- [ ] Implement advanced sorting and filtering

---

### ***** TODO 11. Code Quality Tools

**Static Analysis:**
- Checkstyle for code style
- SpotBugs for bug detection
- SonarQube integration
- JaCoCo for test coverage reporting

---

### ***** TODO 12. Docker & Deployment

**Containerization:**
- Dockerfile for application
- docker-compose.yml with PostgreSQL
- Multi-stage builds for smaller images
- Health check configuration

---

### ***** TODO 13. API Versioning

**Future-proof the API:**
- URL versioning: /api/v1/users
- Header versioning: Accept: application/vnd.api.v1+json

---

### ***** TODO 14. Rate Limiting

**Prevent abuse:**
- Bucket4j or Resilience4j
- Per-user or per-IP limits
- Return 429 Too Many Requests

---

### ***** TODO 15. CORS Configuration

**For frontend integration:**
- Configure allowed origins
- Allowed methods and headers
- Credentials support

---

## License

This is a learning project for educational purposes.
