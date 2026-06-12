package com.project.AllHelp.dto;

public record AdminDashboardStatsDto(
        long totalUsers,
        long totalWorkers,
        long totalRequests,
        long activeAssignments,
        long completedJobs,
        long pendingRequests,
        long assignedRequests,
        long completedRequests
) {
}
