package com.medassist.auth.service.impl;

import com.medassist.auth.dto.request.LoginRequest;
import com.medassist.auth.dto.request.RefreshTokenRequest;
import com.medassist.auth.dto.request.RegisterRequest;
import com.medassist.auth.dto.response.AuthResponse;
import com.medassist.user.dto.response.UserResponse;
import com.medassist.common.exception.BadRequestException;
import com.medassist.common.exception.ConflictException;
import com.medassist.common.exception.ResourceNotFoundException;
import com.medassist.common.exception.UnauthorizedException;
import com.medassist.user.model.User;
import com.medassist.user.repository.UserRepository;
import com.medassist.security.JwtTokenProvider;
import com.medassist.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Authentication service implementation — handles registration, login, and token refresh.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check duplicate email
        if (userRepository.existsByEmail(request.getEmail().toLowerCase())) {
            throw new ConflictException("An account with this email already exists");
        }

        // Check duplicate phone (if provided)
        if (request.getPhoneNumber() != null &&
            userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new ConflictException("An account with this phone number already exists");
        }

        User user = User.builder()
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .email(request.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .bloodGroup(request.getBloodGroup())
                .languagePreference(request.getLanguagePreference() != null
                        ? request.getLanguagePreference() : "en")
                .roles(List.of("ROLE_USER"))
                .active(true)
                .emailVerified(false)
                .build();

        user = userRepository.save(user);
        log.info("New user registered: {} (id={})", user.getEmail(), user.getId());

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpirationMs() / 1000)
                .user(mapToUserResponse(user))
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // Authenticate via Spring Security (verifies password + active status)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase(),
                        request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        if (!user.isActive()) {
            throw new UnauthorizedException("Your account has been deactivated. Please contact support.");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        log.info("User logged in: {} (id={})", user.getEmail(), user.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpirationMs() / 1000)
                .user(mapToUserResponse(user))
                .build();
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        // Validate stored refresh token matches (rotation security)
        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new UnauthorizedException("Refresh token has been revoked");
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(user);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpirationMs() / 1000)
                .user(mapToUserResponse(user))
                .build();
    }

    @Override
    public void logout(String userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setRefreshToken(null);
            userRepository.save(user);
            log.info("User logged out: {}", user.getEmail());
        });
    }

    @Override
    public void initiatePasswordReset(String email) {
        // TODO: generate reset token + send email
        log.info("Password reset requested for: {}", email);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        // TODO: validate token + update password
    }

    @Override
    public void verifyEmail(String token) {
        // TODO: validate verification token
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .bloodGroup(user.getBloodGroup())
                .roles(user.getRoles())
                .profileImage(user.getProfileImage())
                .languagePreference(user.getLanguagePreference())
                .active(user.isActive())
                .emailVerified(user.isEmailVerified())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .build();
    }
}
