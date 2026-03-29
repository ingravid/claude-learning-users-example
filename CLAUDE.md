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
- ✅ `UserManagementApplicationTests` - Context load test

## Architecture Overview

This is a Spring Boot 3.2.5 REST API using **Java 21** with a layered architecture pattern:

**Controller → Service → Repository → Database (H2)**

### Key Architectural Decisions

1. **Java Records for DTOs**: This project uses Java 21 Records (not Lombok) for all DTOs:
   - `UserRequestDto` - Request validation with `@NotBlank` and `@Email` on record components
   - `UserResponseDto` - Immutable response objects
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
   - Generic `Exception` → 500

5. **Testing Strategy**: Comprehensive test coverage across all layers:
   - Unit tests with Mockito for service layer
   - @DataJpaTest for repository layer
   - @SpringBootTest + MockMvc for controller integration tests
   - Mandatory testing for all new features

### Package Structure

- `controller/` - REST endpoints (`@RestController`)
- `service/` - Business logic with interface + implementation pattern
- `repository/` - Spring Data JPA repositories
- `model/` - JPA entities (regular classes with getters/setters)
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

Base path: `/api/users`

- POST `/api/users` - Returns 201 Created
- GET `/api/users/{id}` - Returns 200 OK

All error responses follow the `ErrorResponse` record structure with timestamp, status code, and message.
