package com.project.AllHelp.controller;

import com.project.AllHelp.dto.UserStatsDto;
import com.project.AllHelp.dto.WorkerStatsDto;
import com.project.AllHelp.security.UserPrincipal;
import com.project.AllHelp.service.DashboardStatsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DashboardStatsController {
    private final DashboardStatsService dashboardStatsService;

    public DashboardStatsController(DashboardStatsService dashboardStatsService) {
        this.dashboardStatsService = dashboardStatsService;
    }

    @GetMapping("/user/dashboard/stats")
    @PreAuthorize("hasRole('USER')")
    public UserStatsDto userStats(@AuthenticationPrincipal UserPrincipal principal) {
        return dashboardStatsService.userStats(principal.getId());
    }

    @GetMapping("/worker/dashboard/stats")
    @PreAuthorize("hasRole('WORKER')")
    public WorkerStatsDto workerStats(@AuthenticationPrincipal UserPrincipal principal) {
        return dashboardStatsService.workerStats(principal.getId());
    }
}
