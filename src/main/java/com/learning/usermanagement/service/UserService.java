package com.learning.usermanagement.service;

import com.learning.usermanagement.dto.CreateUserRequestDto;
import com.learning.usermanagement.dto.UserRequestDto;
import com.learning.usermanagement.dto.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponseDto createUser(CreateUserRequestDto request);

    UserResponseDto getUserById(Long id);

    UserResponseDto updateUser(Long id, UserRequestDto request);

    void deleteUser(Long id);

    Page<UserResponseDto> getAllUsers(Pageable pageable);
}
