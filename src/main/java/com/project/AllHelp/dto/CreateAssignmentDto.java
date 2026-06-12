package com.project.AllHelp.dto;

import jakarta.validation.constraints.NotNull;

public record CreateAssignmentDto(
        @NotNull Long requestId,
        @NotNull Long workerId
) {
}
