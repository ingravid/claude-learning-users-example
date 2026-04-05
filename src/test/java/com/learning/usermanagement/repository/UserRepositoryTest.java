package com.learning.usermanagement.repository;

import com.learning.usermanagement.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveUser() {
        // given
        User user = new User(null, "Jane Doe", "jane@example.com", "hashed");

        // when
        User savedUser = userRepository.save(user);

        // then
        assertNotNull(savedUser.getId());
        assertEquals("Jane Doe", savedUser.getName());
        assertEquals("jane@example.com", savedUser.getEmail());
    }

    @Test
    void testFindById_UserExists() {
        // given
        User user = new User(null, "Test User", "test@example.com", "hashed");
        User savedUser = userRepository.save(user);

        // when
        Optional<User> found = userRepository.findById(savedUser.getId());

        // then
        assertTrue(found.isPresent());
        assertEquals("Test User", found.get().getName());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    void testFindById_UserNotFound() {
        // when
        Optional<User> found = userRepository.findById(999L);

        // then
        assertFalse(found.isPresent());
    }

    @Test
    void testExistsByEmail_EmailExists() {
        // given
        User user = new User(null, "Test User", "exists@example.com", "hashed");
        userRepository.save(user);

        // when
        boolean exists = userRepository.existsByEmail("exists@example.com");

        // then
        assertTrue(exists);
    }

    @Test
    void testExistsByEmail_EmailNotExists() {
        // when
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // then
        assertFalse(exists);
    }

    @Test
    void testFindByEmail_UserExists() {
        // given
        User user = new User(null, "Email Test User", "emailtest@example.com", "hashed");
        userRepository.save(user);

        // when
        Optional<User> found = userRepository.findByEmail("emailtest@example.com");

        // then
        assertTrue(found.isPresent());
        assertEquals("Email Test User", found.get().getName());
        assertEquals("emailtest@example.com", found.get().getEmail());
    }

    @Test
    void testEmailUniqueConstraint() {
        // given
        User user1 = new User(null, "User One", "unique@example.com", "hashed");
        userRepository.save(user1);

        User user2 = new User(null, "User Two", "unique@example.com", "hashed");

        // when & then
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(user2);
            userRepository.flush(); // Force immediate persistence to trigger constraint
        });
    }
}
