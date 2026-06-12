package com.project.AllHelp.service;

import com.project.AllHelp.dto.AdminUserSummaryDto;
import com.project.AllHelp.dto.CreateReviewDto;
import com.project.AllHelp.dto.ReviewResponseDto;
import com.project.AllHelp.entity.AppUser;
import com.project.AllHelp.entity.Assignment;
import com.project.AllHelp.entity.AssignmentStatus;
import com.project.AllHelp.entity.NotificationType;
import com.project.AllHelp.entity.RequestStatus;
import com.project.AllHelp.entity.Review;
import com.project.AllHelp.entity.ServiceRequest;
import com.project.AllHelp.entity.WorkerProfile;
import com.project.AllHelp.exception.ApiException;
import com.project.AllHelp.repository.AssignmentRepository;
import com.project.AllHelp.repository.ReviewRepository;
import com.project.AllHelp.repository.ServiceRequestRepository;
import com.project.AllHelp.repository.WorkerProfileRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final AssignmentRepository assignmentRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final AdminRequestService adminRequestService;
    private final WorkerProfileService workerProfileService;
    private final NotificationService notificationService;
    private final ActivityService activityService;

    public ReviewService(
            ReviewRepository reviewRepository,
            ServiceRequestRepository serviceRequestRepository,
            AssignmentRepository assignmentRepository,
            WorkerProfileRepository workerProfileRepository,
            AdminRequestService adminRequestService,
            WorkerProfileService workerProfileService,
            NotificationService notificationService,
            ActivityService activityService
    ) {
        this.reviewRepository = reviewRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.assignmentRepository = assignmentRepository;
        this.workerProfileRepository = workerProfileRepository;
        this.adminRequestService = adminRequestService;
        this.workerProfileService = workerProfileService;
        this.notificationService = notificationService;
        this.activityService = activityService;
    }

    @Transactional
    public ReviewResponseDto createReview(Long userId, CreateReviewDto dto) {
        ServiceRequest request = serviceRequestRepository.findWithUserById(dto.requestId())
                .orElseThrow(() -> new ApiException("Request not found", HttpStatus.NOT_FOUND));

        if (!request.getUser().getId().equals(userId)) {
            throw new ApiException("You can review only your own request", HttpStatus.FORBIDDEN);
        }
        if (request.getStatus() != RequestStatus.COMPLETED) {
            throw new ApiException("Only completed requests can be reviewed", HttpStatus.CONFLICT);
        }
        if (reviewRepository.existsByServiceRequestId(request.getId())) {
            throw new ApiException("Review already submitted for this request", HttpStatus.CONFLICT);
        }

        Assignment assignment = assignmentRepository.findFirstByServiceRequestIdAndAssignmentStatusOrderByAssignedAtDesc(request.getId(), AssignmentStatus.COMPLETED)
                .orElseThrow(() -> new ApiException("Completed assignment not found for this request", HttpStatus.CONFLICT));

        Review review = new Review();
        review.setServiceRequest(request);
        review.setUser(request.getUser());
        review.setWorker(assignment.getWorker());
        review.setRating(dto.rating());
        review.setComment(dto.comment());

        Review savedReview = reviewRepository.save(review);
        refreshWorkerRating(assignment.getWorker().getId());
        activityService.record(request, "Review Added", request.getUser().getFullName());
        notificationService.notify(assignment.getWorker().getUser(), "Review received", request.getUser().getFullName() + " left a " + dto.rating() + "-star review.", NotificationType.REVIEW_RECEIVED);
        return toDto(savedReview);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getUserReviews(Long userId) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getWorkerReviews(Long workerUserId) {
        return reviewRepository.findByWorkerUserIdOrderByCreatedAtDesc(workerUserId).stream()
                .map(this::toDto)
                .toList();
    }

    private void refreshWorkerRating(Long workerId) {
        WorkerProfile worker = workerProfileRepository.findById(workerId)
                .orElseThrow(() -> new ApiException("Worker not found", HttpStatus.NOT_FOUND));
        BigDecimal average = BigDecimal.valueOf(reviewRepository.averageRatingByWorkerId(workerId)).setScale(2, RoundingMode.HALF_UP);
        worker.setRating(average);
        workerProfileRepository.save(worker);
    }

    public ReviewResponseDto toDto(Review review) {
        AppUser user = review.getUser();
        return new ReviewResponseDto(
                review.getId(),
                adminRequestService.toDto(review.getServiceRequest()),
                new AdminUserSummaryDto(user.getId(), user.getFullName(), user.getEmail(), user.getPhone(), user.getProfilePicture(), user.getAddress(), user.getCity()),
                workerProfileService.toDto(review.getWorker()),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}
