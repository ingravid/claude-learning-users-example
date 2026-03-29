package com.learning.usermanagement.service;

import com.learning.usermanagement.dto.UserRequestDto;
import com.learning.usermanagement.dto.UserResponseDto;
import com.learning.usermanagement.exception.DuplicateEmailException;
import com.learning.usermanagement.exception.UserNotFoundException;
import com.learning.usermanagement.model.User;
import com.learning.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testCreateUser_Success() {
        // given
        UserRequestDto request = new UserRequestDto("John Doe", "john@example.com");
        User savedUser = new User(1L, "John Doe", "john@example.com");

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // when
        UserResponseDto result = userService.createUser(request);

        // then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("John Doe", result.name());
        assertEquals("john@example.com", result.email());

        verify(userRepository).existsByEmail("john@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUser_DuplicateEmail_ThrowsException() {
        // given
        UserRequestDto request = new UserRequestDto("John Doe", "duplicate@example.com");
        when(userRepository.existsByEmail("duplicate@example.com")).thenReturn(true);

        // when & then
        assertThrows(DuplicateEmailException.class, () -> {
            userService.createUser(request);
        });

        verify(userRepository).existsByEmail("duplicate@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetUserById_Success() {
        // given
        User user = new User(1L, "Jane Doe", "jane@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        UserResponseDto result = userService.getUserById(1L);

        // then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Jane Doe", result.name());
        assertEquals("jane@example.com", result.email());

        verify(userRepository).findById(1L);
    }

    @Test
    void testGetUserById_NotFound_ThrowsException() {
        // given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(999L);
        });

        assertTrue(exception.getMessage().contains("User not found with id: 999"));
        verify(userRepository).findById(999L);
    }

    @Test
    void testCreateUser_SavesCorrectUserData() {
        // given
        UserRequestDto request = new UserRequestDto("Alice Smith", "alice@example.com");
        User savedUser = new User(2L, "Alice Smith", "alice@example.com");

        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            assertEquals("Alice Smith", user.getName());
            assertEquals("alice@example.com", user.getEmail());
            assertNull(user.getId()); // ID should be null before saving
            return savedUser;
        });

        // when
        UserResponseDto result = userService.createUser(request);

        // then
        assertEquals(2L, result.id());
        assertEquals("Alice Smith", result.name());
        assertEquals("alice@example.com", result.email());
    }

    @Test
    void testCreateUser_CallsRepositoryMethods() {
        // given
        UserRequestDto request = new UserRequestDto("Bob Brown", "bob@example.com");
        User savedUser = new User(3L, "Bob Brown", "bob@example.com");

        when(userRepository.existsByEmail("bob@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // when
        userService.createUser(request);

        // then
        verify(userRepository, times(1)).existsByEmail("bob@example.com");
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testUpdateUser_Success() {
        // given
        Long userId = 1L;
        UserRequestDto request = new UserRequestDto("John Updated", "john.updated@example.com");
        User existingUser = new User(1L, "John Doe", "john@example.com");
        User updatedUser = new User(1L, "John Updated", "john.updated@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("john.updated@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // when
        UserResponseDto result = userService.updateUser(userId, request);

        // then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("John Updated", result.name());
        assertEquals("john.updated@example.com", result.email());

        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmail("john.updated@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUser_UserNotFound_ThrowsException() {
        // given
        Long userId = 999L;
        UserRequestDto request = new UserRequestDto("John Updated", "john.updated@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser(userId, request);
        });

        assertTrue(exception.getMessage().contains("User not found with id: 999"));
        verify(userRepository).findById(userId);
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUser_DuplicateEmail_ThrowsException() {
        // given
        Long userId = 1L;
        UserRequestDto request = new UserRequestDto("John Updated", "duplicate@example.com");
        User existingUser = new User(1L, "John Doe", "john@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("duplicate@example.com")).thenReturn(true);

        // when & then
        DuplicateEmailException exception = assertThrows(DuplicateEmailException.class, () -> {
            userService.updateUser(userId, request);
        });

        assertTrue(exception.getMessage().contains("Email already exists: duplicate@example.com"));
        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmail("duplicate@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUser_SameEmail_Success() {
        // given
        Long userId = 1L;
        UserRequestDto request = new UserRequestDto("John Updated", "john@example.com");
        User existingUser = new User(1L, "John Doe", "john@example.com");
        User updatedUser = new User(1L, "John Updated", "john@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // when
        UserResponseDto result = userService.updateUser(userId, request);

        // then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("John Updated", result.name());
        assertEquals("john@example.com", result.email());

        verify(userRepository).findById(userId);
        verify(userRepository, never()).existsByEmail(anyString()); // Should not check email since it's the same
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testDeleteUser_Success() {
        // given
        Long userId = 1L;
        User existingUser = new User(1L, "John Doe", "john@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        doNothing().when(userRepository).delete(existingUser);

        // when
        userService.deleteUser(userId);

        // then
        verify(userRepository).findById(userId);
        verify(userRepository).delete(existingUser);
    }

    @Test
    void testDeleteUser_UserNotFound_ThrowsException() {
        // given
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(userId);
        });

        assertTrue(exception.getMessage().contains("User not found with id: 999"));
        verify(userRepository).findById(userId);
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void testGetAllUsers_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        User user1 = new User(1L, "John Doe", "john@example.com");
        User user2 = new User(2L, "Jane Smith", "jane@example.com");
        List<User> users = Arrays.asList(user1, user2);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // when
        Page<UserResponseDto> result = userService.getAllUsers(pageable);

        // then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals("John Doe", result.getContent().get(0).name());
        assertEquals("john@example.com", result.getContent().get(0).email());
        assertEquals("Jane Smith", result.getContent().get(1).name());
        assertEquals("jane@example.com", result.getContent().get(1).email());

        verify(userRepository).findAll(pageable);
    }

    @Test
    void testGetAllUsers_EmptyPage() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(userRepository.findAll(pageable)).thenReturn(emptyPage);

        // when
        Page<UserResponseDto> result = userService.getAllUsers(pageable);

        // then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());
        assertTrue(result.isEmpty());

        verify(userRepository).findAll(pageable);
    }
}
