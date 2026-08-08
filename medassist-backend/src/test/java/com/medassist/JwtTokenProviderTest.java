package com.medassist;

import com.medassist.security.JwtTokenProvider;
import com.medassist.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for JwtTokenProvider â€” no Spring context required.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JWT Token Provider Tests")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    // Must be at least 512-bit / 64 characters for HS512
    private static final String SECRET =
            "medassist-ai-x-super-secret-jwt-key-must-be-at-least-64-characters-long-for-hs512!!";
    private static final long ACCESS_EXPIRY  = 3_600_000L;  // 1 hour
    private static final long REFRESH_EXPIRY = 604_800_000L; // 7 days

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(SECRET, ACCESS_EXPIRY, REFRESH_EXPIRY, "medassist-ai-x");
    }

    private User buildTestUser() {
        return User.builder()
                .id("user-123")
                .firstName("Asel")
                .lastName("Perera")
                .email("asel@example.com")
                .roles(List.of("ROLE_USER"))
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should generate a valid access token")
    void shouldGenerateValidAccessToken() {
        User user = buildTestUser();
        String token = jwtTokenProvider.generateAccessToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("Should generate a valid refresh token")
    void shouldGenerateValidRefreshToken() {
        User user = buildTestUser();
        String token = jwtTokenProvider.generateRefreshToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtTokenProvider.validateRefreshToken(token)).isTrue();
    }

    @Test
    @DisplayName("Should extract email from access token")
    void shouldExtractEmailFromToken() {
        User user = buildTestUser();
        String token = jwtTokenProvider.generateAccessToken(user);

        String email = jwtTokenProvider.getEmailFromToken(token);
        assertThat(email).isEqualTo("asel@example.com");
    }

    @Test
    @DisplayName("Should extract userId from access token")
    void shouldExtractUserIdFromToken() {
        User user = buildTestUser();
        String token = jwtTokenProvider.generateAccessToken(user);

        String userId = jwtTokenProvider.getUserIdFromToken(token);
        assertThat(userId).isEqualTo("user-123");
    }

    @Test
    @DisplayName("Should extract roles from token")
    void shouldExtractRolesFromToken() {
        User user = buildTestUser();
        String token = jwtTokenProvider.generateAccessToken(user);

        List<String> roles = jwtTokenProvider.getRolesFromToken(token);
        assertThat(roles).containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("Access token should NOT pass refresh token validation")
    void accessTokenShouldNotPassRefreshValidation() {
        User user = buildTestUser();
        String accessToken = jwtTokenProvider.generateAccessToken(user);

        assertThat(jwtTokenProvider.validateRefreshToken(accessToken)).isFalse();
    }

    @Test
    @DisplayName("Should reject a tampered token")
    void shouldRejectTamperedToken() {
        User user = buildTestUser();
        String token = jwtTokenProvider.generateAccessToken(user);
        // Corrupt the signature section (last part after last dot)
        int lastDot = token.lastIndexOf('.');
        String tamperedToken = token.substring(0, lastDot + 1) + "invalidsignaturexyz";

        assertThat(jwtTokenProvider.validateToken(tamperedToken)).isFalse();
    }

    @Test
    @DisplayName("Should reject an expired token")
    void shouldRejectExpiredToken() throws Exception {
        // Create provider with 1ms expiry
        JwtTokenProvider shortLivedProvider =
                new JwtTokenProvider(SECRET, 1L, 1L, "medassist-ai-x");
        User user = buildTestUser();
        String token = shortLivedProvider.generateAccessToken(user);

        Thread.sleep(10); // wait for expiry
        assertThat(shortLivedProvider.validateToken(token)).isFalse();
    }

    @Test
    @DisplayName("Should return correct expiration in milliseconds")
    void shouldReturnCorrectExpiration() {
        assertThat(jwtTokenProvider.getAccessTokenExpirationMs()).isEqualTo(ACCESS_EXPIRY);
    }
}

