package com.medassist.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.medassist.user.dto.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Authentication response DTO â€” returned on login and register.
 * Matches the frontend api-client.js: data.data.accessToken / data.data.refreshToken
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;        // seconds until access token expiry
    private UserResponse user;     // embedded user info for immediate use
}

