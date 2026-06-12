package com.project.AllHelp.dto;

import com.project.AllHelp.entity.RequestStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateRequestStatusDto(
        @NotNull RequestStatus status
) {
}
