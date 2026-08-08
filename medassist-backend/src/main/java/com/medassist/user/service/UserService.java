package com.medassist.user.service;

import com.medassist.user.dto.request.ProfileUpdateRequest;
import com.medassist.user.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserResponse getCurrentUser(String email);

    UserResponse getUserById(String id);

    UserResponse updateProfile(String userId, ProfileUpdateRequest request);

    String uploadProfileImage(String userId, MultipartFile file);

    void deleteAccount(String userId);

    // Admin
    Page<UserResponse> getAllUsers(String keyword, Pageable pageable);

    void setUserActive(String userId, boolean active);

    void changeUserRole(String userId, String role);
}

