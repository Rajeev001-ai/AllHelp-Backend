package com.project.AllHelp.dto;

import com.project.AllHelp.entity.Role;

public record AuthResponse(
        Long id,
        String fullName,
        String email,
        Role role,
        String accessToken,
        String tokenType,
        String message
) {
}
