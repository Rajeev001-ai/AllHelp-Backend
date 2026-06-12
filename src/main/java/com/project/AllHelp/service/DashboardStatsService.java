package com.project.AllHelp.service;

import com.project.AllHelp.dto.UserStatsDto;
import com.project.AllHelp.dto.WorkerStatsDto;
import com.project.AllHelp.entity.AssignmentStatus;
import com.project.AllHelp.entity.RequestStatus;
import com.project.AllHelp.entity.WorkerProfile;
import com.project.AllHelp.exception.ApiException;
import com.project.AllHelp.repository.AssignmentRepository;
import com.project.AllHelp.repository.ServiceRequestRepository;
import com.project.AllHelp.repository.WorkerProfileRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardStatsService {
    private final ServiceRequestRepository serviceRequestRepository;
    private final AssignmentRepository assignmentRepository;
    private final WorkerProfileRepository workerProfileRepository;

    public DashboardStatsService(ServiceRequestRepository serviceRequestRepository, AssignmentRepository assignmentRepository, WorkerProfileRepository workerProfileRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.assignmentRepository = assignmentRepository;
        this.workerProfileRepository = workerProfileRepository;
    }

    @Transactional(readOnly = true)
    public UserStatsDto userStats(Long userId) {
        long pending = serviceRequestRepository.countByUserIdAndStatus(userId, RequestStatus.PENDING);
        long completed = serviceRequestRepository.countByUserIdAndStatus(userId, RequestStatus.COMPLETED);
        long active = serviceRequestRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .filter(request -> List.of(RequestStatus.ASSIGNED, RequestStatus.IN_PROGRESS).contains(request.getStatus()))
                .count();
        return new UserStatsDto(serviceRequestRepository.countByUserId(userId), active, pending, completed);
    }

    @Transactional(readOnly = true)
    public WorkerStatsDto workerStats(Long workerUserId) {
        WorkerProfile profile = workerProfileRepository.findByUserId(workerUserId)
                .orElseThrow(() -> new ApiException("Worker profile not found", HttpStatus.NOT_FOUND));
        return new WorkerStatsDto(
                assignmentRepository.countByWorkerUserId(workerUserId),
                assignmentRepository.countByWorkerUserIdAndAssignmentStatus(workerUserId, AssignmentStatus.COMPLETED),
                profile.getRating(),
                profile.getAvailability()
        );
    }
}
