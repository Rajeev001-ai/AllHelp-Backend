package com.project.AllHelp.dto;

import java.time.LocalDateTime;

public record ActivityDto(
        Long id,
        String action,
        String performedBy,
        LocalDateTime createdAt
) {
}
