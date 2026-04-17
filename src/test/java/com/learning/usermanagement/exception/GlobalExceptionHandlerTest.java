package com.learning.usermanagement.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleUserNotFoundException() {
        // given
        UserNotFoundException exception = new UserNotFoundException("User not found with id: 999");

        // when
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUserNotFoundException(exception);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().status());
        assertEquals("User not found with id: 999", response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void testHandleDuplicateEmailException() {
        // given
        DuplicateEmailException exception = new DuplicateEmailException("Email already exists: test@example.com");

        // when
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDuplicateEmailException(exception);

        // then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().status());
        assertEquals("Email already exists: test@example.com", response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void testHandleValidationException() {
        // given
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("userRequestDto", "email", "Email must be valid");
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        // when
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Email must be valid", response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void testHandleAccessDeniedException() {
        // given
        AccessDeniedException exception = new AccessDeniedException("Access is denied");

        // when
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccessDeniedException(exception);

        // then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(403, response.getBody().status());
        assertEquals("Access denied: insufficient permissions", response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void testHandleGenericException() {
        // given
        Exception exception = new Exception("Some unexpected error");

        // when
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception);

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().status());
        assertEquals("An unexpected error occurred", response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void testErrorResponseStructure() {
        // given
        UserNotFoundException exception = new UserNotFoundException("Test error");

        // when
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUserNotFoundException(exception);

        // then
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);

        // Verify all fields are present and non-null
        assertNotNull(errorResponse.timestamp(), "timestamp should not be null");
        assertNotNull(errorResponse.message(), "message should not be null");

        // Verify field values
        assertEquals(404, errorResponse.status());
        assertEquals("Test error", errorResponse.message());
    }
}
