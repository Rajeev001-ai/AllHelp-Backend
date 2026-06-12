package com.project.AllHelp.dto;

import com.project.AllHelp.entity.Availability;
import jakarta.validation.constraints.Min;

public record UpdateWorkerProfileDto(
        String fullName,
        String skills,
        @Min(0) Integer experience,
        String bio,
        String city,
        Availability availability
) {
}
