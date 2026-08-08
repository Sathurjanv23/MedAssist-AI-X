package com.medassist.user.service.impl;

import com.medassist.user.dto.request.ProfileUpdateRequest;
import com.medassist.user.dto.response.UserResponse;
import com.medassist.common.exception.BadRequestException;
import com.medassist.common.exception.ResourceNotFoundException;
import com.medassist.user.model.User;
import com.medassist.user.repository.UserRepository;
import com.medassist.user.service.UserService;
import com.medassist.storage.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.Period;

/**
 * User service implementation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final S3StorageService s3StorageService;

    @Override
    @Cacheable(value = "user", key = "#email")
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return mapToResponse(user);
    }

    @Override
    @Cacheable(value = "user", key = "#id")
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return mapToResponse(user);
    }

    @Override
    @CacheEvict(value = "user", allEntries = true)
    public UserResponse updateProfile(String userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName().trim());
        if (request.getLastName() != null) user.setLastName(request.getLastName().trim());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getBloodGroup() != null) user.setBloodGroup(request.getBloodGroup());
        if (request.getLanguagePreference() != null) user.setLanguagePreference(request.getLanguagePreference());
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(LocalDate.parse(request.getDateOfBirth()));
        }

        user = userRepository.save(user);
        log.info("Profile updated for user: {}", userId);
        return mapToResponse(user);
    }

    @Override
    @CacheEvict(value = "user", allEntries = true)
    public String uploadProfileImage(String userId, MultipartFile file) {
        if (file == null || file.isEmpty()) throw new BadRequestException("No file provided");

        String imageUrl = s3StorageService.uploadProfileImage(userId, file);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        user.setProfileImage(imageUrl);
        userRepository.save(user);

        return imageUrl;
    }

    @Override
    @CacheEvict(value = "user", allEntries = true)
    public void deleteAccount(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        user.setActive(false);
        userRepository.save(user);
        log.info("Account deactivated for user: {}", userId);
    }

    @Override
    public Page<UserResponse> getAllUsers(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return userRepository.searchUsers(keyword, pageable).map(this::mapToResponse);
        }
        return userRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    @CacheEvict(value = "user", allEntries = true)
    public void setUserActive(String userId, boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        user.setActive(active);
        userRepository.save(user);
    }

    @Override
    @CacheEvict(value = "user", allEntries = true)
    public void changeUserRole(String userId, String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
            userRepository.save(user);
        }
    }

    private UserResponse mapToResponse(User user) {
        Integer age = null;
        if (user.getDateOfBirth() != null) {
            age = Period.between(user.getDateOfBirth(), LocalDate.now()).getYears();
        }
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
                .updatedAt(user.getUpdatedAt())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .age(age)
                .build();
    }
}

