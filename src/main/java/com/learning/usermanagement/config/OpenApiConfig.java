package com.learning.usermanagement.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "User Management REST API",
                version = "1.0.0",
                description = "Spring Boot REST application for managing users with CRUD operations. " +
                        "Built with Java 21 and Spring Boot 3.2.5, featuring pagination support, " +
                        "input validation, and comprehensive error handling.",
                contact = @Contact(
                        name = "User Management API",
                        email = "support@example.com"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Local Development Server"
                )
        }
)
public class OpenApiConfig {
}
