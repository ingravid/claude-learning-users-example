package com.learning.usermanagement.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

    String generateToken(String email);

    String extractEmail(String token);

    boolean isTokenExpired(String token);

    boolean validateToken(String token, UserDetails userDetails);
}
