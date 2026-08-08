package com.medassist.auth.service;

import com.medassist.auth.dto.request.LoginRequest;
import com.medassist.auth.dto.request.RefreshTokenRequest;
import com.medassist.auth.dto.request.RegisterRequest;
import com.medassist.auth.dto.response.AuthResponse;

/**
 * Authentication service interface.
 */
public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void logout(String userId);

    void initiatePasswordReset(String email);

    void resetPassword(String token, String newPassword);

    void verifyEmail(String token);
}

