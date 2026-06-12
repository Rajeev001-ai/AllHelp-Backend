package com.project.AllHelp.service;

import com.project.AllHelp.dto.UpdateUserProfileDto;
import com.project.AllHelp.dto.UploadResultDto;
import com.project.AllHelp.dto.UserProfileDto;
import com.project.AllHelp.entity.AppUser;
import com.project.AllHelp.exception.ApiException;
import com.project.AllHelp.repository.AppUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserProfileService {
    private final AppUserRepository appUserRepository;
    private final MediaUploadService mediaUploadService;

    public UserProfileService(AppUserRepository appUserRepository, MediaUploadService mediaUploadService) {
        this.appUserRepository = appUserRepository;
        this.mediaUploadService = mediaUploadService;
    }

    @Transactional(readOnly = true)
    public UserProfileDto getProfile(Long userId) {
        return appUserRepository.findById(userId).map(this::toDto)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public UserProfileDto updateProfile(Long userId, UpdateUserProfileDto dto) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        appUserRepository.findByEmail(dto.email())
                .filter(existing -> !existing.getId().equals(userId))
                .ifPresent(existing -> { throw new ApiException("Email is already in use", HttpStatus.CONFLICT); });
        appUserRepository.findByPhone(dto.phone())
                .filter(existing -> !existing.getId().equals(userId))
                .ifPresent(existing -> { throw new ApiException("Phone is already in use", HttpStatus.CONFLICT); });

        user.setFullName(dto.fullName());
        user.setEmail(dto.email());
        user.setPhone(dto.phone());
        user.setAddress(dto.address());
        user.setCity(dto.city());
        return toDto(appUserRepository.save(user));
    }

    @Transactional
    public UserProfileDto uploadProfileImage(Long userId, MultipartFile file) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
        UploadResultDto upload = mediaUploadService.uploadImage(file, "allhelp/profile");
        user.setProfilePicture(upload.url());
        return toDto(appUserRepository.save(user));
    }

    private UserProfileDto toDto(AppUser user) {
        return new UserProfileDto(user.getId(), user.getProfilePicture(), user.getFullName(), user.getEmail(), user.getPhone(), user.getAddress(), user.getCity());
    }
}
