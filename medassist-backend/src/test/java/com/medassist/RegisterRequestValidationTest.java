package com.medassist;

import com.medassist.auth.dto.request.RegisterRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for RegisterRequest DTO validation constraints.
 * Validates all Jakarta Bean Validation annotations are configured correctly.
 */
@DisplayName("RegisterRequest Validation Tests")
class RegisterRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    private RegisterRequest validRequest() {
        return RegisterRequest.builder()
                .firstName("Asel")
                .lastName("Perera")
                .email("asel@example.com")
                .password("SecurePass1@")
                .gender("FEMALE")
                .phoneNumber("+94771234567")
                .bloodGroup("O+")
                .languagePreference("en")
                .build();
    }

    @Test
    @DisplayName("Valid request should have no violations")
    void validRequestShouldPassValidation() {
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(validRequest());
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should reject blank email")
    void shouldRejectBlankEmail() {
        RegisterRequest request = validRequest();
        request.setEmail("");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    @DisplayName("Should reject invalid email format")
    void shouldRejectInvalidEmail() {
        RegisterRequest request = validRequest();
        request.setEmail("not-an-email");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    @DisplayName("Should reject weak password without uppercase")
    void shouldRejectWeakPassword() {
        RegisterRequest request = validRequest();
        request.setPassword("weakpassword1@");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }

    @Test
    @DisplayName("Should reject short password")
    void shouldRejectShortPassword() {
        RegisterRequest request = validRequest();
        request.setPassword("Ab1@");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }

    @Test
    @DisplayName("Should reject invalid blood group")
    void shouldRejectInvalidBloodGroup() {
        RegisterRequest request = validRequest();
        request.setBloodGroup("X+");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("bloodGroup"));
    }

    @Test
    @DisplayName("Should reject invalid gender")
    void shouldRejectInvalidGender() {
        RegisterRequest request = validRequest();
        request.setGender("INVALID");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("gender"));
    }

    @Test
    @DisplayName("Should accept all valid blood groups")
    void shouldAcceptAllValidBloodGroups() {
        String[] validGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        for (String group : validGroups) {
            RegisterRequest request = validRequest();
            request.setBloodGroup(group);
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
            assertThat(violations).as("Blood group %s should be valid", group)
                    .noneMatch(v -> v.getPropertyPath().toString().equals("bloodGroup"));
        }
    }
}

