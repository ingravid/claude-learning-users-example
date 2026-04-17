package com.learning.usermanagement.controller;

import com.learning.usermanagement.model.Role;
import com.learning.usermanagement.model.User;
import com.learning.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(roles = "ADMIN")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testCreateUser_Success_Returns201() throws Exception {
        String requestBody = """
            {
                "name": "John Doe",
                "email": "john@example.com"
            }
            """;

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void testCreateUser_InvalidEmail_Returns400() throws Exception {
        String requestBody = """
            {
                "name": "John Doe",
                "email": "invalid-email"
            }
            """;

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testCreateUser_BlankName_Returns400() throws Exception {
        String requestBody = """
            {
                "name": "",
                "email": "test@example.com"
            }
            """;

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Name cannot be blank"));
    }

    @Test
    void testCreateUser_BlankEmail_Returns400() throws Exception {
        String requestBody = """
            {
                "name": "John Doe",
                "email": ""
            }
            """;

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testCreateUser_DuplicateEmail_Returns409() throws Exception {
        User existingUser = new User(null, "Existing User", "duplicate@example.com", "hashed", Role.USER);
        userRepository.save(existingUser);

        String requestBody = """
            {
                "name": "New User",
                "email": "duplicate@example.com"
            }
            """;

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Email already exists: duplicate@example.com"));
    }

    @Test
    void testGetUserById_Success_Returns200() throws Exception {
        User user = new User(null, "Jane Doe", "jane@example.com", "hashed", Role.USER);
        User savedUser = userRepository.save(user);

        mockMvc.perform(get("/api/users/" + savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.email").value("jane@example.com"));
    }

    @Test
    void testGetUserById_NotFound_Returns404() throws Exception {
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User not found with id: 999"));
    }

    @Test
    void testCreateUser_ResponseBodyCorrect() throws Exception {
        String requestBody = """
            {
                "name": "Alice Smith",
                "email": "alice@example.com"
            }
            """;

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").isString())
                .andExpect(jsonPath("$.email").isString())
                .andExpect(jsonPath("$.name").value("Alice Smith"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void testGetUserById_ResponseBodyCorrect() throws Exception {
        User user = new User(null, "Bob Brown", "bob@example.com", "hashed", Role.USER);
        User savedUser = userRepository.save(user);

        mockMvc.perform(get("/api/users/" + savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").isString())
                .andExpect(jsonPath("$.email").isString())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.name").value("Bob Brown"))
                .andExpect(jsonPath("$.email").value("bob@example.com"));
    }

    @Test
    void testErrorResponse_HasCorrectStructure() throws Exception {
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").isString())
                .andExpect(jsonPath("$.status").isNumber())
                .andExpect(jsonPath("$.message").isString());
    }

    // --- Role-based authorization tests ---

    @Test
    @WithMockUser(roles = "USER")
    void testCreateUser_UserRole_Returns403() throws Exception {
        String requestBody = """
            {
                "name": "John Doe",
                "email": "john@example.com"
            }
            """;

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUpdateUser_UserRole_Returns403() throws Exception {
        User user = new User(null, "Jane Doe", "jane@example.com", "hashed", Role.USER);
        User savedUser = userRepository.save(user);

        String requestBody = """
            {
                "name": "Jane Updated",
                "email": "jane.updated@example.com"
            }
            """;

        mockMvc.perform(put("/api/users/" + savedUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testDeleteUser_UserRole_Returns403() throws Exception {
        User user = new User(null, "Jane Doe", "jane@example.com", "hashed", Role.USER);
        User savedUser = userRepository.save(user);

        mockMvc.perform(delete("/api/users/" + savedUser.getId()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }
}
