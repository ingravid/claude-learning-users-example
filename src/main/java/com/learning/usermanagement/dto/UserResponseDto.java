package com.learning.usermanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User information response")
public record UserResponseDto(
        @Schema(description = "User unique identifier", example = "1")
        Long id,

        @Schema(description = "User's full name", example = "John Doe")
        String name,

        @Schema(description = "User's email address", example = "john@example.com")
        String email
) {
}
