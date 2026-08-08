package com.medassist.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * User document â€” core identity entity for MedAssist AI X.
 *
 * <p>Embedded vs Reference design decision:
 * - Medical profile, health data â†’ separate documents (large, frequently updated independently)
 * - Roles â†’ embedded as List<String> (rarely change, small)
 * - Profile image â†’ URL string referencing AWS S3 object
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Field("first_name")
    private String firstName;

    @Field("last_name")
    private String lastName;

    @Indexed(unique = true)
    @Field("email")
    private String email;

    @Field("password")
    private String password;

    @Indexed(sparse = true)
    @Field("phone_number")
    private String phoneNumber;

    @Field("date_of_birth")
    private LocalDate dateOfBirth;

    @Field("gender")
    private String gender;

    @Field("blood_group")
    private String bloodGroup;

    @Field("roles")
    @Builder.Default
    private List<String> roles = List.of("ROLE_USER");

    @Field("profile_image")
    private String profileImage;

    @Field("language_preference")
    @Builder.Default
    private String languagePreference = "en";

    @Field("is_active")
    @Builder.Default
    private boolean active = true;

    @Field("is_email_verified")
    @Builder.Default
    private boolean emailVerified = false;

    @Field("email_verification_token")
    private String emailVerificationToken;

    @Field("password_reset_token")
    private String passwordResetToken;

    @Field("password_reset_expiry")
    private LocalDateTime passwordResetExpiry;

    @Field("refresh_token")
    private String refreshToken;

    @Field("last_login")
    private LocalDateTime lastLogin;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
}

