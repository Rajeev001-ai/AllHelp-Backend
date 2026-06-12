package com.project.AllHelp.dto;

import com.project.AllHelp.entity.AssignmentStatus;
import java.time.LocalDateTime;

public record AssignmentResponseDto(
        Long id,
        AdminRequestResponseDto serviceRequest,
        WorkerProfileDto worker,
        AdminUserSummaryDto assignedByAdmin,
        LocalDateTime assignedAt,
        LocalDateTime acceptedAt,
        LocalDateTime completedAt,
        String notes,
        AssignmentStatus assignmentStatus
) {
}
