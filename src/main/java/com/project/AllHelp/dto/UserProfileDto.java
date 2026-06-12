package com.project.AllHelp.dto;

public record UserProfileDto(
        Long id,
        String profilePicture,
        String fullName,
        String email,
        String phone,
        String address,
        String city
) {
}
