package com.learning.usermanagement.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Error response structure")
public record ErrorResponse(
        @Schema(description = "Timestamp when the error occurred", example = "2024-03-31T10:15:30")
        LocalDateTime timestamp,

        @Schema(description = "HTTP status code", example = "404")
        int status,

        @Schema(description = "Error message describing what went wrong", example = "User not found with id: 1")
        String message
) {
}
