package com.project.AllHelp.dto;

import com.project.AllHelp.entity.Availability;
import java.math.BigDecimal;

public record WorkerStatsDto(
        long assignedJobs,
        long completedJobs,
        BigDecimal rating,
        Availability availability
) {
}
