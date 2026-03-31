package com.learning.usermanagement.controller;

import com.learning.usermanagement.dto.UserRequestDto;
import com.learning.usermanagement.dto.UserResponseDto;
import com.learning.usermanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Validated
@Tag(name = "User Management", description = "Operations for managing users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Get all users",
            description = "Retrieve paginated list of all users"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved users")
    @GetMapping
    public Page<UserResponseDto> getAllUsers(
            @Parameter(description = "Page number (zero-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of users per page", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userService.getAllUsers(pageable);
    }

    @Operation(
            summary = "Create user",
            description = "Create a new user with name and email"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input - validation failed"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@Valid @RequestBody UserRequestDto request) {
        return userService.createUser(request);
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieve a specific user by their ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public UserResponseDto getUserById(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long id) {
        return userService.getUserById(id);
    }

    @Operation(
            summary = "Update user",
            description = "Update an existing user's information"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    @PutMapping("/{id}")
    public UserResponseDto updateUser(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto request) {
        return userService.updateUser(id, request);
    }

    @Operation(
            summary = "Delete user",
            description = "Delete a user by their ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long id) {
        userService.deleteUser(id);
    }
}
