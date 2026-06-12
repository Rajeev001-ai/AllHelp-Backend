package com.project.AllHelp.dto;

import com.project.AllHelp.entity.Role;
import java.time.LocalDateTime;

public record AdminUserDetailsDto(
        Long id,
        String profilePicture,
        String fullName,
        String email,
        String phone,
        String address,
        String city,
        Role role,
        LocalDateTime createdAt,
        long totalRequests,
        long pendingRequests,
        long completedRequests
) {
}
