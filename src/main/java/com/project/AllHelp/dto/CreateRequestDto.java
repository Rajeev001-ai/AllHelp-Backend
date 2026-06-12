package com.project.AllHelp.dto;

import com.project.AllHelp.entity.Urgency;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalTime;

public record CreateRequestDto(
        @NotBlank String category,
        @NotBlank String description,
        @NotBlank String address,
        String city,
        LocalDate preferredDate,
        LocalTime preferredTime,
        Urgency urgency
) {
}
