# User Management REST API

A Spring Boot REST application for managing users with CRUD operations, built with Java 21 and H2 in-memory database.

## Features

- Complete CRUD operations (Create, Read, Update, Delete)
- Get all users with pagination support
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

## Prerequisites

- Java 21 or higher
- Maven 3.6+ (or use the included Maven wrapper)

## Project Structure

```
src/main/java/com/learning/usermanagement/
├── UserManagementApplication.java    # Main application class
├── controller/                        # REST API endpoints
├── service/                           # Business logic layer
├── repository/                        # Data access layer
├── model/                             # JPA entities
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
# Create a user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name": "Alice", "email": "alice@example.com"}'

# Get all users
curl http://localhost:8080/api/users

# Access H2 console
# Open browser: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:userdb
# Username: sa
# Password: (leave empty)
```

## API Summary

| Method | Endpoint | Description | Request Body | Success Response |
|--------|----------|-------------|--------------|------------------|
| GET | `/api/users` | Get all users (paginated) | - | 200 OK |
| POST | `/api/users` | Create a new user | `{"name": "...", "email": "..."}` | 201 Created |
| GET | `/api/users/{id}` | Get user by ID | - | 200 OK |
| PUT | `/api/users/{id}` | Update user | `{"name": "...", "email": "..."}` | 200 OK |
| DELETE | `/api/users/{id}` | Delete user | - | 204 No Content |
| GET | `/api/greetings` | Get greeting message | - | 200 OK |

**Common Error Responses:**
- `400 Bad Request` - Validation failed
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
# Get all users (with pagination)
curl "http://localhost:8080/api/users?page=0&size=10"

# Get all users (default pagination)
curl http://localhost:8080/api/users

# Create a user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name": "Jane Doe", "email": "jane@example.com"}'

# Get a user by ID
curl http://localhost:8080/api/users/1

# Update a user
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "Jane Updated", "email": "jane.updated@example.com"}'

# Update user name only (keep same email)
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "Jane Smith", "email": "jane@example.com"}'

# Delete a user
curl -X DELETE http://localhost:8080/api/users/1

# Error examples:

# Create a user with validation error
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name": "", "email": "invalid"}'

# Create a user with duplicate email
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name": "John Smith", "email": "jane@example.com"}'

# Try to get non-existent user
curl http://localhost:8080/api/users/999

# Try to update non-existent user
curl -X PUT http://localhost:8080/api/users/999 \
  -H "Content-Type: application/json" \
  -d '{"name": "Ghost", "email": "ghost@example.com"}'

# Try to delete non-existent user
curl -X DELETE http://localhost:8080/api/users/999

# Get greeting message
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

## License

This is a learning project for educational purposes.
