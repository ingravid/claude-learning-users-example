package com.learning.usermanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "User creation/update request")
public record UserRequestDto(
        @Schema(description = "User's full name", example = "John Doe")
        @NotBlank(message = "Name cannot be blank")
        String name,

        @Schema(description = "User's email address", example = "john@example.com")
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email must be valid")
        String email
) {
}
