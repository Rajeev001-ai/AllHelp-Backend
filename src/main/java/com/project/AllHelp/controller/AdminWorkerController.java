package com.project.AllHelp.controller;

import com.project.AllHelp.dto.AssignmentResponseDto;
import com.project.AllHelp.dto.CreateAssignmentDto;
import com.project.AllHelp.dto.PortfolioDto;
import com.project.AllHelp.dto.UpdateWorkerAvailabilityDto;
import com.project.AllHelp.dto.WorkerProfileDto;
import com.project.AllHelp.entity.Availability;
import com.project.AllHelp.security.UserPrincipal;
import com.project.AllHelp.service.AssignmentService;
import com.project.AllHelp.service.PortfolioService;
import com.project.AllHelp.service.WorkerProfileService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminWorkerController {

    private final WorkerProfileService workerProfileService;
    private final AssignmentService assignmentService;
    private final PortfolioService portfolioService;

    public AdminWorkerController(WorkerProfileService workerProfileService, AssignmentService assignmentService, PortfolioService portfolioService) {
        this.workerProfileService = workerProfileService;
        this.assignmentService = assignmentService;
        this.portfolioService = portfolioService;
    }

    @GetMapping("/workers")
    public List<WorkerProfileDto> getWorkers(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Availability availability,
            @RequestParam(required = false) Boolean verified,
            @RequestParam(required = false) String skill
    ) {
        return workerProfileService.getWorkers(city, availability, verified, skill);
    }

    @GetMapping("/workers/{id}")
    public WorkerProfileDto getWorker(@PathVariable Long id) {
        return workerProfileService.getWorker(id);
    }

    @GetMapping("/workers/{id}/portfolio")
    public List<PortfolioDto> getWorkerPortfolio(@PathVariable Long id) {
        return portfolioService.getPortfolioByWorkerId(id);
    }

    @PatchMapping("/workers/{id}/verify")
    public WorkerProfileDto verifyWorker(@PathVariable Long id) {
        return workerProfileService.verifyWorker(id);
    }

    @PatchMapping("/workers/{id}/availability")
    public WorkerProfileDto updateAvailability(@PathVariable Long id, @Valid @RequestBody UpdateWorkerAvailabilityDto dto) {
        return workerProfileService.updateAvailability(id, dto);
    }

    @PostMapping("/assignments")
    public AssignmentResponseDto assignWorker(@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody CreateAssignmentDto dto) {
        return assignmentService.assignWorker(principal.getId(), dto);
    }

    @GetMapping("/assignments")
    public List<AssignmentResponseDto> getAssignments() {
        return assignmentService.getAssignments();
    }

    @GetMapping("/completion-requests")
    public List<AssignmentResponseDto> getCompletionRequests() {
        return assignmentService.getCompletionRequests();
    }

    @GetMapping("/assignments/{id}")
    public AssignmentResponseDto getAssignment(@PathVariable Long id) {
        return assignmentService.getAssignment(id);
    }

    @PatchMapping("/assignments/{id}/verify-completion")
    public AssignmentResponseDto verifyCompletion(@PathVariable Long id) {
        return assignmentService.verifyCompletion(id);
    }
}
