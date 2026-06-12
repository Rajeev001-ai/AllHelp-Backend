package com.project.AllHelp.dto;

public record AdminUserSummaryDto(
        Long id,
        String fullName,
        String email,
        String phone,
        String profilePicture,
        String address,
        String city
) {
}
