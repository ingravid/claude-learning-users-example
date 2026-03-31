package com.learning.usermanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/greetings")
@Tag(name = "Greeting", description = "Simple greeting endpoint")
public class GreetingController {

    @Operation(
            summary = "Get greeting",
            description = "Returns a simple greeting message"
    )
    @ApiResponse(responseCode = "200", description = "Greeting message returned")
    @GetMapping
    public String greetUser() {
        return "Hello user";
    }
}
