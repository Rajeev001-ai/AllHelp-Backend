package com.project.AllHelp.controller;

import com.project.AllHelp.dto.UpdateUserProfileDto;
import com.project.AllHelp.dto.UserProfileDto;
import com.project.AllHelp.security.UserPrincipal;
import com.project.AllHelp.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user/profile")
@PreAuthorize("hasRole('USER')")
public class UserProfileController {
    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public UserProfileDto getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        return userProfileService.getProfile(principal.getId());
    }

    @PutMapping
    public UserProfileDto updateProfile(@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody UpdateUserProfileDto dto) {
        return userProfileService.updateProfile(principal.getId(), dto);
    }

    @PostMapping("/image")
    public UserProfileDto uploadImage(@AuthenticationPrincipal UserPrincipal principal, @RequestParam("image") MultipartFile image) {
        return userProfileService.uploadProfileImage(principal.getId(), image);
    }
}
