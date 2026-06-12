package com.project.AllHelp.service;

import com.project.AllHelp.dto.RequestDetailsDto;
import com.project.AllHelp.entity.Assignment;
import com.project.AllHelp.entity.Review;
import com.project.AllHelp.entity.Role;
import com.project.AllHelp.entity.ServiceRequest;
import com.project.AllHelp.exception.ApiException;
import com.project.AllHelp.repository.AssignmentRepository;
import com.project.AllHelp.repository.ReviewRepository;
import com.project.AllHelp.repository.ServiceRequestRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RequestDetailsService {
    private final ServiceRequestRepository serviceRequestRepository;
    private final AssignmentRepository assignmentRepository;
    private final ReviewRepository reviewRepository;
    private final AdminRequestService adminRequestService;
    private final AssignmentService assignmentService;
    private final ReviewService reviewService;
    private final ActivityService activityService;

    public RequestDetailsService(
            ServiceRequestRepository serviceRequestRepository,
            AssignmentRepository assignmentRepository,
            ReviewRepository reviewRepository,
            AdminRequestService adminRequestService,
            AssignmentService assignmentService,
            ReviewService reviewService,
            ActivityService activityService
    ) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.assignmentRepository = assignmentRepository;
        this.reviewRepository = reviewRepository;
        this.adminRequestService = adminRequestService;
        this.assignmentService = assignmentService;
        this.reviewService = reviewService;
        this.activityService = activityService;
    }

    @Transactional(readOnly = true)
    public RequestDetailsDto getDetails(Long principalId, Role role, Long requestId) {
        ServiceRequest request = serviceRequestRepository.findWithUserById(requestId)
                .orElseThrow(() -> new ApiException("Request not found", HttpStatus.NOT_FOUND));
        Assignment assignment = assignmentRepository.findFirstByServiceRequestIdOrderByAssignedAtDesc(requestId).orElse(null);

        boolean allowed = role == Role.ADMIN
                || (role == Role.USER && request.getUser().getId().equals(principalId))
                || (role == Role.WORKER && assignment != null && assignment.getWorker().getUser().getId().equals(principalId));

        if (!allowed) {
            throw new ApiException("You are not allowed to view this request", HttpStatus.FORBIDDEN);
        }

        Review review = reviewRepository.findByServiceRequestId(requestId).orElse(null);
        return new RequestDetailsDto(
                adminRequestService.toDto(request),
                assignment == null ? null : assignmentService.toDto(assignment),
                review == null ? null : reviewService.toDto(review),
                activityService.getRequestActivities(requestId)
        );
    }
}
