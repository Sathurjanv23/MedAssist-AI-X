package com.medassist.user.service.impl;

import com.medassist.user.dto.request.MedicalProfileRequest;
import com.medassist.common.exception.BadRequestException;
import com.medassist.user.model.MedicalProfile;
import com.medassist.user.repository.MedicalProfileRepository;
import com.medassist.user.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final MedicalProfileRepository profileRepository;

    @Override
    public MedicalProfile getProfile(String userId) {
        return profileRepository.findByUserId(userId)
                .orElseGet(MedicalProfile::new);
    }

    @Override
    public MedicalProfile getOrCreateProfile(String userId) {
        return profileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    MedicalProfile profile = MedicalProfile.builder().userId(userId).build();
                    return profileRepository.save(profile);
                });
    }

    @Override
    public MedicalProfile updateProfile(String userId, MedicalProfileRequest request) {
        if (request == null || !hasAnyProfileContent(request)) {
            throw new BadRequestException("At least one medical profile field must be provided");
        }

        MedicalProfile profile = getOrCreateProfile(userId);

        if (request.getHeightCm() != null) profile.setHeightCm(request.getHeightCm());
        if (request.getWeightKg() != null) {
            profile.setWeightKg(request.getWeightKg());
            if (request.getHeightCm() != null) {
                double heightM = request.getHeightCm() / 100.0;
                profile.setBmi(Math.round((request.getWeightKg() / (heightM * heightM)) * 10.0) / 10.0);
            }
        }
        if (request.getBloodPressure() != null) {
            MedicalProfile.BloodPressure bp = new MedicalProfile.BloodPressure();
            bp.setSystolic(request.getBloodPressure().getSystolic());
            bp.setDiastolic(request.getBloodPressure().getDiastolic());
            bp.setCategory(categorizeBP(bp.getSystolic(), bp.getDiastolic()));
            profile.setBloodPressure(bp);
        }
        if (request.getAllergies() != null) profile.setAllergies(request.getAllergies());
        if (request.getChronicDiseases() != null) profile.setChronicDiseases(request.getChronicDiseases());
        if (request.getCurrentMedications() != null) profile.setCurrentMedications(request.getCurrentMedications());
        if (request.getPastSurgeries() != null) profile.setPastSurgeries(request.getPastSurgeries());
        if (request.getFamilyHistory() != null) profile.setFamilyHistory(request.getFamilyHistory());

        if (request.getLifestyle() != null) {
            MedicalProfileRequest.LifestyleRequest ls = request.getLifestyle();
            MedicalProfile.Lifestyle lifestyle = MedicalProfile.Lifestyle.builder()
                    .smokingStatus(ls.getSmokingStatus())
                    .alcoholConsumption(ls.getAlcoholConsumption())
                    .exerciseFrequency(ls.getExerciseFrequency())
                    .dietType(ls.getDietType())
                    .sleepHoursPerNight(ls.getSleepHoursPerNight())
                    .stressLevel(ls.getStressLevel())
                    .occupation(ls.getOccupation())
                    .build();
            profile.setLifestyle(lifestyle);
        }

        if (request.getEmergencyContact() != null) {
            MedicalProfileRequest.EmergencyContactRequest ec = request.getEmergencyContact();
            MedicalProfile.EmergencyContact contact = MedicalProfile.EmergencyContact.builder()
                    .name(ec.getName())
                    .relationship(ec.getRelationship())
                    .phoneNumber(ec.getPhoneNumber())
                    .email(ec.getEmail())
                    .build();
            profile.setEmergencyContact(contact);
        }

        return profileRepository.save(profile);
    }

    @Override
    public void deleteProfile(String userId) {
        profileRepository.deleteByUserId(userId);
    }

    private String categorizeBP(Integer systolic, Integer diastolic) {
        if (systolic == null || diastolic == null) return "Unknown";
        if (systolic < 120 && diastolic < 80) return "Normal";
        if (systolic < 130 && diastolic < 80) return "Elevated";
        if (systolic < 140 || diastolic < 90) return "High Stage 1";
        return "High Stage 2";
    }

    private boolean hasAnyProfileContent(MedicalProfileRequest request) {
        return request.getHeightCm() != null
                || request.getWeightKg() != null
                || request.getBloodPressure() != null
                || request.getAllergies() != null
                || request.getChronicDiseases() != null
                || request.getCurrentMedications() != null
                || request.getPastSurgeries() != null
                || request.getFamilyHistory() != null
                || request.getLifestyle() != null
                || request.getEmergencyContact() != null;
    }
}

