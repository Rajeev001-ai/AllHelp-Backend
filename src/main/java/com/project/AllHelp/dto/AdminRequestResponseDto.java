package com.project.AllHelp.dto;

import com.project.AllHelp.entity.RequestStatus;
import com.project.AllHelp.entity.Urgency;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record AdminRequestResponseDto(
        Long id,
        AdminUserSummaryDto user,
        String category,
        String description,
        String address,
        String city,
        LocalDate preferredDate,
        LocalTime preferredTime,
        Urgency urgency,
        RequestStatus status,
        LocalDateTime createdAt
) {
}
