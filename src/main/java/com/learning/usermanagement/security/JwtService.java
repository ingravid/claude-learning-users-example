package com.learning.usermanagement.security;

public interface JwtService {

    String generateToken(String email);
}
