package com.project.AllHelp.dto;

import java.time.LocalDateTime;

public record ReviewResponseDto(
        Long id,
        AdminRequestResponseDto serviceRequest,
        AdminUserSummaryDto user,
        WorkerProfileDto worker,
        Integer rating,
        String comment,
        LocalDateTime createdAt
) {
}
