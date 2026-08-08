package com.medassist.user.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Profile update request â€” all fields optional (PATCH semantics).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {

    @Size(min = 2, max = 50)
    private String firstName;

    @Size(min = 1, max = 50)
    private String lastName;

    @Pattern(regexp = "^[+]?[(]?[0-9]{3}[)]?[-\\s.]?[0-9]{3}[-\\s.]?[0-9]{4,6}$")
    private String phoneNumber;

    private String dateOfBirth; // ISO date: yyyy-MM-dd

    @Pattern(regexp = "^(MALE|FEMALE|OTHER|PREFER_NOT_TO_SAY)$")
    private String gender;

    @Pattern(regexp = "^(A\\+|A-|B\\+|B-|AB\\+|AB-|O\\+|O-)$")
    private String bloodGroup;

    @Pattern(regexp = "^(en|ta|si)$")
    private String languagePreference;
}

