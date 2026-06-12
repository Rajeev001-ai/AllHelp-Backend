package com.project.AllHelp.dto;

import com.project.AllHelp.entity.Availability;
import jakarta.validation.constraints.NotNull;

public record UpdateWorkerAvailabilityDto(@NotNull Availability availability) {
}
