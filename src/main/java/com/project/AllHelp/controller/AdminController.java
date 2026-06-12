package com.project.AllHelp.controller;

import com.project.AllHelp.dto.AdminDashboardStatsDto;
import com.project.AllHelp.dto.AdminAnalyticsDto;
import com.project.AllHelp.dto.AdminRequestResponseDto;
import com.project.AllHelp.dto.AdminUserDetailsDto;
import com.project.AllHelp.dto.UpdateRequestStatusDto;
import com.project.AllHelp.entity.RequestStatus;
import com.project.AllHelp.entity.Urgency;
import com.project.AllHelp.service.AdminRequestService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminRequestService adminRequestService;

    public AdminController(AdminRequestService adminRequestService) {
        this.adminRequestService = adminRequestService;
    }

    @GetMapping("/dashboard/stats")
    public AdminDashboardStatsDto getStats() {
        return adminRequestService.getStats();
    }

    @GetMapping("/users")
    public List<AdminUserDetailsDto> getUsers() {
        return adminRequestService.getUsers();
    }

    @GetMapping("/analytics")
    public AdminAnalyticsDto getAnalytics() {
        return adminRequestService.getAnalytics();
    }

    @GetMapping("/requests")
    public List<AdminRequestResponseDto> getRequests(
            @RequestParam(required = false) RequestStatus status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Urgency urgency,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return adminRequestService.getRequests(status, category, urgency, date);
    }

    @GetMapping("/requests/{id}")
    public AdminRequestResponseDto getRequest(@PathVariable Long id) {
        return adminRequestService.getRequest(id);
    }

    @GetMapping("/requests/pending")
    public List<AdminRequestResponseDto> getPendingRequests() {
        return adminRequestService.getRequestsByStatus(RequestStatus.PENDING);
    }

    @GetMapping("/requests/assigned")
    public List<AdminRequestResponseDto> getAssignedRequests() {
        return adminRequestService.getRequestsByStatus(RequestStatus.ASSIGNED);
    }

    @PatchMapping("/requests/{id}/status")
    public AdminRequestResponseDto updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateRequestStatusDto dto) {
        return adminRequestService.updateStatus(id, dto);
    }
}
