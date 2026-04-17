# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Run Commands

```bash
# Build the project
./mvnw clean package

# Run the application
./mvnw spring-boot:run

# Run tests
./mvnw test

# Compile only
./mvnw clean compile
```

The application starts on port 8080. H2 console is available at `http://localhost:8080/h2-console` with JDBC URL `jdbc:h2:mem:userdb` (username: `sa`, no password).

## Testing Commands

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=UserServiceTest

# Run tests with coverage (if jacoco added)
./mvnw test jacoco:report

# Run only unit tests (exclude integration tests)
./mvnw test -Dtest=*Test

# Run only integration tests
./mvnw test -Dtest=*IntegrationTest
```

## Testing Philosophy

**MANDATORY**: All new features and bug fixes MUST include corresponding tests before merging.

### Testing Requirements

1. **Unit Tests Required**:
   - All service layer methods must have unit tests with mocked dependencies
   - Business logic validation (success cases + error cases)
   - Exception handling verification

2. **Integration Tests Required**:
   - Repository methods with custom queries must have @DataJpaTest tests
   - REST endpoints must have @SpringBootTest + MockMvc integration tests
   - Test all HTTP status codes (200, 201, 400, 404, 409, 500)
   - Test request/response body structure

3. **Test Coverage Expectations**:
   - Service layer: 100% method coverage
   - Controller layer: All endpoints covered
   - Repository layer: All custom queries covered
   - Exception handlers: All exception types covered

4. **Test Naming Convention**:
   - Unit tests: `<ClassName>Test.java`
   - Integration tests: `<ClassName>IntegrationTest.java`
   - Method pattern: `test<MethodName>_<Scenario>_<ExpectedResult>()`
   - Example: `testCreateUser_DuplicateEmail_ThrowsException()`

5. **When Adding New Features**:
   - Write tests FIRST (TDD approach recommended) OR alongside implementation
   - Never commit code without corresponding tests
   - Verify `./mvnw test` passes before pushing

### Current Test Coverage

- ✅ `UserServiceTest` - Service layer unit tests (Mockito)
- ✅ `UserRepositoryTest` - Repository integration tests (@DataJpaTest)
- ✅ `GlobalExceptionHandlerTest` - Exception handler unit tests
- ✅ `UserControllerIntegrationTest` - REST API integration tests (MockMvc)
- ✅ `GreetingControllerIntegrationTest` - Greeting endpoint integration test (MockMvc)
- ✅ `UserManagementApplicationTests` - Context load test

## Architecture Overview

This is a Spring Boot 3.2.5 REST API using **Java 21** with a layered architecture pattern:

**Controller → Service → Repository → Database (H2)**

### Key Architectural Decisions

1. **Java Records for DTOs**: This project uses Java 21 Records (not Lombok) for all DTOs:
   - `UserRequestDto` - Request validation with `@NotBlank` and `@Email` on record components
   - `UserResponseDto` - Immutable response objects
   - `RegisterRequestDto` - Registration payload (name, email, password)
   - `LoginRequestDto` - Login payload (email, password)
   - `AuthResponseDto` - JWT token wrapper returned after auth
   - `ErrorResponse` - Consistent error format across all endpoints

2. **JPA Entities**: Regular classes (not Records) are used for JPA entities because JPA requires:
   - No-arg constructor
   - Mutable fields for Hibernate to set via reflection
   - See `User.java` for the pattern

3. **Manual Constructor Injection**: Services and controllers use explicit constructors for dependency injection instead of field injection or `@Autowired`

4. **Global Exception Handling**: `GlobalExceptionHandler` (@RestControllerAdvice) provides centralized error handling:
   - `UserNotFoundException` → 404
   - `DuplicateEmailException` → 409
   - `MethodArgumentNotValidException` → 400
   - `BadCredentialsException` → 401
   - Generic `Exception` → 500

5. **JWT Security**: Stateless Bearer-token authentication using HS256 (jjwt 0.12.6):
   - `JwtService`/`JwtServiceImpl` - token generation and validation (interface + impl pattern)
   - `JwtAuthenticationFilter` - extracts and validates Bearer tokens on every request
   - `JwtAuthenticationEntryPoint` - returns JSON 401 for unauthenticated requests
   - `UserDetailsServiceImpl` - loads `UserDetails` by email for Spring Security
   - Public routes: `/api/auth/**`, `/api/greetings`, `/h2-console/**`, Swagger UI
   - Protected routes: all `/api/users/**` endpoints require a valid JWT

6. **Testing Strategy**: Comprehensive test coverage across all layers:
   - Unit tests with Mockito for service layer
   - @DataJpaTest for repository layer
   - @SpringBootTest + MockMvc for controller integration tests
   - Mandatory testing for all new features

### Package Structure

- `config/` - Spring configuration (`SecurityConfig`, `OpenApiConfig`)
- `controller/` - REST endpoints (`@RestController`)
- `service/` - Business logic with interface + implementation pattern
- `security/` - JWT filter, entry point, `UserDetailsServiceImpl`, `JwtService`/`JwtServiceImpl`
- `repository/` - Spring Data JPA repositories
- `model/` - JPA entities and enums (`User`, `Role`)
- `dto/` - Java Records for data transfer
- `exception/` - Custom exceptions and global handler

### Database Configuration

The H2 database is configured with:
- `hibernate.ddl-auto: create-drop` - Schema recreated on each restart (development mode)
- `show-sql: true` - SQL logging enabled
- `format_sql: true` - Pretty-printed SQL in logs

### Validation Strategy

Bean Validation (Jakarta) is applied at the DTO level on Record components. The controller validates using `@Valid` on `@RequestBody`. Business validation (like duplicate email checking) occurs in the service layer.

### REST API Design

**Authentication (public — no token required):**
- `POST /api/auth/register` - Register account, returns JWT (201 Created)
- `POST /api/auth/login` - Login, returns JWT (200 OK)

**User Management (protected — requires `Authorization: Bearer <token>`):**
- `GET  /api/users` - List all users, paginated (200 OK)
- `POST /api/users` - Create user (201 Created)
- `GET  /api/users/{id}` - Get user by ID (200 OK)
- `PUT  /api/users/{id}` - Update user (200 OK)
- `DELETE /api/users/{id}` - Delete user (204 No Content)

**Other (public):**
- `GET /api/greetings` - Returns greeting string (200 OK)

All error responses follow the `ErrorResponse` record structure with `timestamp`, `status`, and `message`.
