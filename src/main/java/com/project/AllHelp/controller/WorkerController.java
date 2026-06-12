package com.project.AllHelp.controller;

import com.project.AllHelp.dto.AssignmentResponseDto;
import com.project.AllHelp.dto.PortfolioDto;
import com.project.AllHelp.dto.ReviewResponseDto;
import com.project.AllHelp.dto.UpdateWorkerProfileDto;
import com.project.AllHelp.dto.WorkerProfileDto;
import com.project.AllHelp.entity.AssignmentStatus;
import com.project.AllHelp.security.UserPrincipal;
import com.project.AllHelp.service.AssignmentService;
import com.project.AllHelp.service.PortfolioService;
import com.project.AllHelp.service.ReviewService;
import com.project.AllHelp.service.WorkerProfileService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/worker")
@PreAuthorize("hasRole('WORKER')")
public class WorkerController {

    private final AssignmentService assignmentService;
    private final WorkerProfileService workerProfileService;
    private final ReviewService reviewService;
    private final PortfolioService portfolioService;

    public WorkerController(AssignmentService assignmentService, WorkerProfileService workerProfileService, ReviewService reviewService, PortfolioService portfolioService) {
        this.assignmentService = assignmentService;
        this.workerProfileService = workerProfileService;
        this.reviewService = reviewService;
        this.portfolioService = portfolioService;
    }

    @GetMapping("/jobs")
    public List<AssignmentResponseDto> getJobs(@AuthenticationPrincipal UserPrincipal principal) {
        return assignmentService.getWorkerJobs(principal.getId());
    }

    @GetMapping("/jobs/completed")
    public List<AssignmentResponseDto> getCompletedJobs(@AuthenticationPrincipal UserPrincipal principal) {
        return assignmentService.getWorkerCompletedJobs(principal.getId());
    }

    @GetMapping("/jobs/{id}")
    public AssignmentResponseDto getJob(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return assignmentService.getWorkerJob(principal.getId(), id);
    }

    @PatchMapping("/jobs/{id}/accept")
    public AssignmentResponseDto accept(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return assignmentService.updateWorkerJobStatus(principal.getId(), id, AssignmentStatus.ACCEPTED);
    }

    @PatchMapping("/assignments/{id}/accept")
    public AssignmentResponseDto acceptAssignment(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return assignmentService.updateWorkerJobStatus(principal.getId(), id, AssignmentStatus.ACCEPTED);
    }

    @PatchMapping("/jobs/{id}/reject")
    public AssignmentResponseDto reject(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return assignmentService.updateWorkerJobStatus(principal.getId(), id, AssignmentStatus.REJECTED);
    }

    @PatchMapping("/assignments/{id}/reject")
    public AssignmentResponseDto rejectAssignment(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return assignmentService.updateWorkerJobStatus(principal.getId(), id, AssignmentStatus.REJECTED);
    }

    @PatchMapping("/jobs/{id}/start")
    public AssignmentResponseDto start(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return assignmentService.updateWorkerJobStatus(principal.getId(), id, AssignmentStatus.IN_PROGRESS);
    }

    @PatchMapping("/assignments/{id}/start")
    public AssignmentResponseDto startAssignment(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return assignmentService.updateWorkerJobStatus(principal.getId(), id, AssignmentStatus.IN_PROGRESS);
    }

    @PatchMapping("/jobs/{id}/complete")
    public AssignmentResponseDto complete(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return assignmentService.updateWorkerJobStatus(principal.getId(), id, AssignmentStatus.COMPLETED_PENDING_VERIFICATION);
    }

    @PatchMapping("/assignments/{id}/complete")
    public AssignmentResponseDto completeAssignment(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return assignmentService.updateWorkerJobStatus(principal.getId(), id, AssignmentStatus.COMPLETED_PENDING_VERIFICATION);
    }

    @GetMapping("/profile")
    public WorkerProfileDto getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        return workerProfileService.getProfileForUser(principal.getId());
    }

    @GetMapping("/reviews")
    public List<ReviewResponseDto> getReviews(@AuthenticationPrincipal UserPrincipal principal) {
        return reviewService.getWorkerReviews(principal.getId());
    }

    @PutMapping("/profile")
    public WorkerProfileDto updateProfile(@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody UpdateWorkerProfileDto dto) {
        return workerProfileService.updateProfileForUser(principal.getId(), dto);
    }

    @PostMapping("/profile/image")
    public WorkerProfileDto uploadProfileImage(@AuthenticationPrincipal UserPrincipal principal, @RequestParam("image") MultipartFile image) {
        return workerProfileService.uploadProfileImage(principal.getId(), image);
    }

    @PostMapping("/portfolio")
    public PortfolioDto addPortfolio(@AuthenticationPrincipal UserPrincipal principal, @RequestParam("image") MultipartFile image, @RequestParam(required = false) String title, @RequestParam(required = false) String description) {
        return portfolioService.create(principal.getId(), image, title, description);
    }

    @GetMapping("/portfolio")
    public List<PortfolioDto> getPortfolio(@AuthenticationPrincipal UserPrincipal principal) {
        return portfolioService.getWorkerPortfolio(principal.getId());
    }

    @DeleteMapping("/portfolio/{id}")
    public void deletePortfolio(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        portfolioService.delete(principal.getId(), id);
    }
}
