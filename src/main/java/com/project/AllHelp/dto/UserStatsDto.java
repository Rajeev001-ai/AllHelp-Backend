package com.project.AllHelp.dto;

public record UserStatsDto(
        long totalRequests,
        long activeRequests,
        long pendingRequests,
        long completedRequests
) {
}
