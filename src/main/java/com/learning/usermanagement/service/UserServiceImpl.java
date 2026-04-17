package com.learning.usermanagement.service;

import com.learning.usermanagement.dto.CreateUserRequestDto;
import com.learning.usermanagement.dto.UserRequestDto;
import com.learning.usermanagement.dto.UserResponseDto;
import com.learning.usermanagement.exception.DuplicateEmailException;
import com.learning.usermanagement.exception.UserNotFoundException;
import com.learning.usermanagement.model.Role;
import com.learning.usermanagement.model.User;
import com.learning.usermanagement.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public UserResponseDto createUser(CreateUserRequestDto request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException("Email already exists: " + request.email());
        }

        // Create and save user entity with hashed password
        User user = new User(null, request.name(), request.email(), passwordEncoder.encode(request.password()), Role.USER);
        User savedUser = userRepository.save(user);

        // Convert to response DTO
        return new UserResponseDto(savedUser.getId(), savedUser.getName(), savedUser.getEmail());
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        // Find user or throw exception
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        // Convert to response DTO
        return new UserResponseDto(user.getId(), user.getName(), user.getEmail());
    }

    @Transactional
    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto request) {
        // Find existing user or throw exception
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        // Check if new email already exists (excluding current user's email)
        if (!existingUser.getEmail().equals(request.email()) &&
            userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException("Email already exists: " + request.email());
        }

        // Update user fields
        existingUser.setName(request.name());
        existingUser.setEmail(request.email());

        // Save updated user
        User updatedUser = userRepository.save(existingUser);

        // Convert to response DTO
        return new UserResponseDto(updatedUser.getId(), updatedUser.getName(), updatedUser.getEmail());
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        // Find existing user or throw exception
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        // Delete the user
        userRepository.delete(existingUser);
    }

    @Override
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        // Fetch all users with pagination
        Page<User> userPage = userRepository.findAll(pageable);

        // Convert to response DTOs
        return userPage.map(user ->
                new UserResponseDto(user.getId(), user.getName(), user.getEmail())
        );
    }
}
