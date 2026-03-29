package com.learning.usermanagement.controller;

import com.learning.usermanagement.model.User;
import com.learning.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
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
        // given
        String requestBody = """
            {
                "name": "John Doe",
                "email": "john@example.com"
            }
            """;

        // when & then
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
        // given
        String requestBody = """
            {
                "name": "John Doe",
                "email": "invalid-email"
            }
            """;

        // when & then
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
        // given
        String requestBody = """
            {
                "name": "",
                "email": "test@example.com"
            }
            """;

        // when & then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Name cannot be blank"));
    }

    @Test
    void testCreateUser_BlankEmail_Returns400() throws Exception {
        // given
        String requestBody = """
            {
                "name": "John Doe",
                "email": ""
            }
            """;

        // when & then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testCreateUser_DuplicateEmail_Returns409() throws Exception {
        // given - create first user
        User existingUser = new User(null, "Existing User", "duplicate@example.com");
        userRepository.save(existingUser);

        String requestBody = """
            {
                "name": "New User",
                "email": "duplicate@example.com"
            }
            """;

        // when & then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Email already exists: duplicate@example.com"));
    }

    @Test
    void testGetUserById_Success_Returns200() throws Exception {
        // given
        User user = new User(null, "Jane Doe", "jane@example.com");
        User savedUser = userRepository.save(user);

        // when & then
        mockMvc.perform(get("/api/users/" + savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.email").value("jane@example.com"));
    }

    @Test
    void testGetUserById_NotFound_Returns404() throws Exception {
        // when & then
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User not found with id: 999"));
    }

    @Test
    void testCreateUser_ResponseBodyCorrect() throws Exception {
        // given
        String requestBody = """
            {
                "name": "Alice Smith",
                "email": "alice@example.com"
            }
            """;

        // when & then
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
        // given
        User user = new User(null, "Bob Brown", "bob@example.com");
        User savedUser = userRepository.save(user);

        // when & then
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
        // when & then - test 404 error response structure
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
}
