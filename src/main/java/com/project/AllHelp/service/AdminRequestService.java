package com.project.AllHelp.service;

import com.project.AllHelp.dto.AdminDashboardStatsDto;
import com.project.AllHelp.dto.AdminAnalyticsDto;
import com.project.AllHelp.dto.AdminRequestResponseDto;
import com.project.AllHelp.dto.AdminUserSummaryDto;
import com.project.AllHelp.dto.AdminUserDetailsDto;
import com.project.AllHelp.dto.UpdateRequestStatusDto;
import com.project.AllHelp.entity.AppUser;
import com.project.AllHelp.entity.AssignmentStatus;
import com.project.AllHelp.entity.RequestStatus;
import com.project.AllHelp.entity.Role;
import com.project.AllHelp.entity.ServiceRequest;
import com.project.AllHelp.entity.Urgency;
import com.project.AllHelp.exception.ApiException;
import com.project.AllHelp.repository.AppUserRepository;
import com.project.AllHelp.repository.AssignmentRepository;
import com.project.AllHelp.repository.ReviewRepository;
import com.project.AllHelp.repository.ServiceRequestRepository;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminRequestService {

    private final AppUserRepository appUserRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final AssignmentRepository assignmentRepository;
    private final ReviewRepository reviewRepository;

    public AdminRequestService(AppUserRepository appUserRepository, ServiceRequestRepository serviceRequestRepository, AssignmentRepository assignmentRepository, ReviewRepository reviewRepository) {
        this.appUserRepository = appUserRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.assignmentRepository = assignmentRepository;
        this.reviewRepository = reviewRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminUserDetailsDto> getUsers() {
        return appUserRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(user -> user.getRole() == Role.USER)
                .map(this::toUserDetailsDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminAnalyticsDto getAnalytics() {
        return new AdminAnalyticsDto(
                appUserRepository.countByRole(Role.USER),
                appUserRepository.countByRole(Role.WORKER),
                serviceRequestRepository.count(),
                serviceRequestRepository.countByStatus(RequestStatus.PENDING),
                serviceRequestRepository.countByStatus(RequestStatus.ASSIGNED),
                serviceRequestRepository.countByStatus(RequestStatus.IN_PROGRESS),
                serviceRequestRepository.countByStatus(RequestStatus.COMPLETED),
                serviceRequestRepository.countByStatus(RequestStatus.CANCELLED),
                assignmentRepository.countByAssignmentStatusIn(List.of(AssignmentStatus.ASSIGNED, AssignmentStatus.ACCEPTED, AssignmentStatus.IN_PROGRESS, AssignmentStatus.COMPLETED_PENDING_VERIFICATION)),
                assignmentRepository.countByAssignmentStatusIn(List.of(AssignmentStatus.COMPLETED)),
                reviewRepository.count()
        );
    }

    @Transactional(readOnly = true)
    public AdminDashboardStatsDto getStats() {
        return new AdminDashboardStatsDto(
                appUserRepository.countByRole(Role.USER),
                appUserRepository.countByRole(Role.WORKER),
                serviceRequestRepository.count(),
                assignmentRepository.countByAssignmentStatusIn(List.of(AssignmentStatus.ASSIGNED, AssignmentStatus.ACCEPTED, AssignmentStatus.IN_PROGRESS, AssignmentStatus.COMPLETED_PENDING_VERIFICATION)),
                assignmentRepository.countByAssignmentStatusIn(List.of(AssignmentStatus.COMPLETED)),
                serviceRequestRepository.countByStatus(RequestStatus.PENDING),
                serviceRequestRepository.countByStatus(RequestStatus.ASSIGNED),
                serviceRequestRepository.countByStatus(RequestStatus.COMPLETED)
        );
    }

    @Transactional(readOnly = true)
    public List<AdminRequestResponseDto> getRequests(RequestStatus status, String category, Urgency urgency, LocalDate date) {
        return serviceRequestRepository.findAll((root, query, criteriaBuilder) -> {
                    root.fetch("user");
                    query.orderBy(criteriaBuilder.desc(root.get("createdAt")));

                    List<Predicate> predicates = new ArrayList<>();
                    if (status != null) {
                        predicates.add(criteriaBuilder.equal(root.get("status"), status));
                    }
                    if (category != null && !category.isBlank()) {
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("category")), "%" + category.toLowerCase() + "%"));
                    }
                    if (urgency != null) {
                        predicates.add(criteriaBuilder.equal(root.get("urgency"), urgency));
                    }
                    if (date != null) {
                        predicates.add(criteriaBuilder.equal(root.get("preferredDate"), date));
                    }

                    return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
                }).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminRequestResponseDto getRequest(Long id) {
        return serviceRequestRepository.findWithUserById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ApiException("Request not found", HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<AdminRequestResponseDto> getRequestsByStatus(RequestStatus status) {
        return serviceRequestRepository.findByStatusOrderByCreatedAtDesc(status).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public AdminRequestResponseDto updateStatus(Long id, UpdateRequestStatusDto dto) {
        ServiceRequest request = serviceRequestRepository.findWithUserById(id)
                .orElseThrow(() -> new ApiException("Request not found", HttpStatus.NOT_FOUND));

        request.setStatus(dto.status());
        return toDto(serviceRequestRepository.save(request));
    }

    public AdminRequestResponseDto toDto(ServiceRequest request) {
        AppUser user = request.getUser();
        AdminUserSummaryDto userDto = new AdminUserSummaryDto(user.getId(), user.getFullName(), user.getEmail(), user.getPhone(), user.getProfilePicture(), user.getAddress(), user.getCity());

        return new AdminRequestResponseDto(
                request.getId(),
                userDto,
                request.getCategory(),
                request.getDescription(),
                request.getAddress(),
                request.getCity(),
                request.getPreferredDate(),
                request.getPreferredTime(),
                request.getUrgency(),
                request.getStatus(),
                request.getCreatedAt()
        );
    }

    private AdminUserDetailsDto toUserDetailsDto(AppUser user) {
        return new AdminUserDetailsDto(
                user.getId(),
                user.getProfilePicture(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress(),
                user.getCity(),
                user.getRole(),
                user.getCreatedAt(),
                serviceRequestRepository.countByUserId(user.getId()),
                serviceRequestRepository.countByUserIdAndStatus(user.getId(), RequestStatus.PENDING),
                serviceRequestRepository.countByUserIdAndStatus(user.getId(), RequestStatus.COMPLETED)
        );
    }
}
