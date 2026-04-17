package com.learning.usermanagement.service;

import com.learning.usermanagement.dto.AuthResponseDto;
import com.learning.usermanagement.dto.LoginRequestDto;
import com.learning.usermanagement.dto.RegisterRequestDto;
import com.learning.usermanagement.exception.DuplicateEmailException;
import com.learning.usermanagement.model.Role;
import com.learning.usermanagement.model.User;
import com.learning.usermanagement.repository.UserRepository;
import com.learning.usermanagement.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponseDto register(RegisterRequestDto request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException("Email already in use: " + request.email());
        }
        User user = new User(null, request.name(), request.email(), passwordEncoder.encode(request.password()), Role.USER);
        userRepository.save(user);
        return new AuthResponseDto(jwtService.generateToken(user.getEmail()));
    }

    @Override
    public AuthResponseDto authenticate(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        return new AuthResponseDto(jwtService.generateToken(user.getEmail()));
    }
}
