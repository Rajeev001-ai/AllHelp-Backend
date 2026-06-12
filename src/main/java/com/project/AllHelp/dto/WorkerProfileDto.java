package com.project.AllHelp.dto;

import com.project.AllHelp.entity.Availability;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WorkerProfileDto(
        Long id,
        AdminUserSummaryDto user,
        String skills,
        Integer experience,
        String bio,
        String city,
        Availability availability,
        Boolean verified,
        BigDecimal rating,
        Long totalReviews,
        Integer completedJobs,
        LocalDateTime createdAt
) {
}
