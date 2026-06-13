package com.project.AllHelp.dto;

import java.time.LocalDateTime;

public record ContactMessageDto(
        Long id,
        String fullName,
        String phone,
        String email,
        String message,
        boolean read,
        LocalDateTime createdAt
) {
}
