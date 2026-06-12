package com.project.AllHelp.dto;

public record AdminAnalyticsDto(
        long totalUsers,
        long totalWorkers,
        long totalRequests,
        long pendingRequests,
        long assignedRequests,
        long inProgressRequests,
        long completedRequests,
        long cancelledRequests,
        long activeAssignments,
        long completedAssignments,
        long totalReviews
) {
}
