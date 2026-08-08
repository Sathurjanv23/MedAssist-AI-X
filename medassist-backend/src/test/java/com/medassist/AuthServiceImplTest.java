package com.medassist;

import com.medassist.auth.dto.request.LoginRequest;
import com.medassist.auth.dto.request.RefreshTokenRequest;
import com.medassist.auth.dto.response.AuthResponse;
import com.medassist.common.exception.BadRequestException;
import com.medassist.common.exception.UnauthorizedException;
import com.medassist.user.model.User;
import com.medassist.user.repository.UserRepository;
import com.medassist.security.JwtTokenProvider;
import com.medassist.auth.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthServiceImpl using Mockito.
 * No database or Spring context needed.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Auth Service Tests")
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user-001")
                .firstName("Nuwan")
                .lastName("Silva")
                .email("nuwan@example.com")
                .password("$2a$12$encoded")
                .roles(List.of("ROLE_USER"))
                .active(true)
                .emailVerified(false)
                .build();
    }

    // â”€â”€ Register â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Test
    @DisplayName("Should register a new user successfully")
    void shouldRegisterNewUser() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$12$hashed");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtTokenProvider.generateAccessToken(any())).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(any())).thenReturn("refresh-token");
        when(jwtTokenProvider.getAccessTokenExpirationMs()).thenReturn(3600000L);

        var request = com.medassist.auth.dto.request.RegisterRequest.builder()
                .firstName("Nuwan")
                .lastName("Silva")
                .email("nuwan@example.com")
                .password("SecurePass1@")
                .gender("MALE")
                .build();

        AuthResponse response = authService.register(request);

        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when email already exists")
    void shouldThrowWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("nuwan@example.com")).thenReturn(true);

        var request = com.medassist.auth.dto.request.RegisterRequest.builder()
                .firstName("Nuwan").lastName("Silva")
                .email("nuwan@example.com").password("SecurePass1@")
                .gender("MALE").build();

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already exists");
    }

    // â”€â”€ Login â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Test
    @DisplayName("Should login and return tokens")
    void shouldLoginSuccessfully() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(org.springframework.security.core.Authentication.class));
        when(userRepository.findByEmail("nuwan@example.com")).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateAccessToken(any())).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(any())).thenReturn("refresh-token");
        when(jwtTokenProvider.getAccessTokenExpirationMs()).thenReturn(3600000L);
        when(userRepository.save(any())).thenReturn(testUser);

        LoginRequest request = new LoginRequest("nuwan@example.com", "SecurePass1@");
        AuthResponse response = authService.login(request);

        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getUser().getEmail()).isEqualTo("nuwan@example.com");
    }

    @Test
    @DisplayName("Should throw UnauthorizedException for inactive user login")
    void shouldRejectInactiveUserLogin() {
        testUser.setActive(false);
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail("nuwan@example.com")).thenReturn(Optional.of(testUser));

        LoginRequest request = new LoginRequest("nuwan@example.com", "SecurePass1@");
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("deactivated");
    }

    // â”€â”€ Refresh Token â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Test
    @DisplayName("Should refresh tokens with valid refresh token")
    void shouldRefreshToken() {
        testUser.setRefreshToken("valid-refresh-token");
        when(jwtTokenProvider.validateRefreshToken("valid-refresh-token")).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken("valid-refresh-token")).thenReturn("nuwan@example.com");
        when(userRepository.findByEmail("nuwan@example.com")).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateAccessToken(any())).thenReturn("new-access-token");
        when(jwtTokenProvider.generateRefreshToken(any())).thenReturn("new-refresh-token");
        when(jwtTokenProvider.getAccessTokenExpirationMs()).thenReturn(3600000L);
        when(userRepository.save(any())).thenReturn(testUser);

        RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");
        AuthResponse response = authService.refreshToken(request);

        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");
    }

    @Test
    @DisplayName("Should throw UnauthorizedException for invalid refresh token")
    void shouldRejectInvalidRefreshToken() {
        when(jwtTokenProvider.validateRefreshToken("bad-token")).thenReturn(false);

        assertThatThrownBy(() -> authService.refreshToken(new RefreshTokenRequest("bad-token")))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when refresh token is revoked")
    void shouldRejectRevokedRefreshToken() {
        testUser.setRefreshToken("stored-token");
        when(jwtTokenProvider.validateRefreshToken("different-token")).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken("different-token")).thenReturn("nuwan@example.com");
        when(userRepository.findByEmail("nuwan@example.com")).thenReturn(Optional.of(testUser));

        // Token is valid but doesn't match stored token (rotation attack)
        assertThatThrownBy(() -> authService.refreshToken(new RefreshTokenRequest("different-token")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("revoked");
    }

    // â”€â”€ Logout â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Test
    @DisplayName("Should invalidate refresh token on logout")
    void shouldLogoutAndClearToken() {
        testUser.setRefreshToken("some-token");
        when(userRepository.findById("user-001")).thenReturn(Optional.of(testUser));

        authService.logout("user-001");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getRefreshToken()).isNull();
    }
}

