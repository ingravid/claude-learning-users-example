package com.learning.usermanagement.service;

import com.learning.usermanagement.dto.AuthResponseDto;
import com.learning.usermanagement.dto.LoginRequestDto;
import com.learning.usermanagement.dto.RegisterRequestDto;

public interface AuthService {

    AuthResponseDto register(RegisterRequestDto request);

    AuthResponseDto authenticate(LoginRequestDto request);
}
