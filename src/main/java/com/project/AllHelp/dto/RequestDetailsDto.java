package com.project.AllHelp.dto;

import java.util.List;

public record RequestDetailsDto(
        AdminRequestResponseDto request,
        AssignmentResponseDto assignment,
        ReviewResponseDto review,
        List<ActivityDto> activities
) {
}
