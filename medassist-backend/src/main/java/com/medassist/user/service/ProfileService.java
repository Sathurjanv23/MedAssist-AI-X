package com.medassist.user.service;

import com.medassist.user.dto.request.MedicalProfileRequest;
import com.medassist.user.model.MedicalProfile;

public interface ProfileService {
    MedicalProfile getProfile(String userId);
    MedicalProfile getOrCreateProfile(String userId);
    MedicalProfile updateProfile(String userId, MedicalProfileRequest request);
    void deleteProfile(String userId);
}

