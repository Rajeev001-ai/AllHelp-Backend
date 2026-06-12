package com.project.AllHelp.service;

import com.project.AllHelp.dto.AdminRequestResponseDto;
import com.project.AllHelp.dto.AdminUserSummaryDto;
import com.project.AllHelp.dto.AssignmentResponseDto;
import com.project.AllHelp.dto.CreateAssignmentDto;
import com.project.AllHelp.entity.AppUser;
import com.project.AllHelp.entity.Assignment;
import com.project.AllHelp.entity.AssignmentStatus;
import com.project.AllHelp.entity.Availability;
import com.project.AllHelp.entity.NotificationType;
import com.project.AllHelp.entity.RequestStatus;
import com.project.AllHelp.entity.ServiceRequest;
import com.project.AllHelp.entity.WorkerProfile;
import com.project.AllHelp.exception.ApiException;
import com.project.AllHelp.repository.AppUserRepository;
import com.project.AllHelp.repository.AssignmentRepository;
import com.project.AllHelp.repository.ServiceRequestRepository;
import com.project.AllHelp.repository.WorkerProfileRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final AppUserRepository appUserRepository;
    private final AdminRequestService adminRequestService;
    private final WorkerProfileService workerProfileService;
    private final NotificationService notificationService;
    private final ActivityService activityService;

    public AssignmentService(
            AssignmentRepository assignmentRepository,
            ServiceRequestRepository serviceRequestRepository,
            WorkerProfileRepository workerProfileRepository,
            AppUserRepository appUserRepository,
            AdminRequestService adminRequestService,
            WorkerProfileService workerProfileService,
            NotificationService notificationService,
            ActivityService activityService
    ) {
        this.assignmentRepository = assignmentRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.workerProfileRepository = workerProfileRepository;
        this.appUserRepository = appUserRepository;
        this.adminRequestService = adminRequestService;
        this.workerProfileService = workerProfileService;
        this.notificationService = notificationService;
        this.activityService = activityService;
    }

    @Transactional
    public AssignmentResponseDto assignWorker(Long adminUserId, CreateAssignmentDto dto) {
        ServiceRequest request = serviceRequestRepository.findById(dto.requestId())
                .orElseThrow(() -> new ApiException("Request not found", HttpStatus.NOT_FOUND));
        WorkerProfile worker = workerProfileRepository.findWithUserById(dto.workerId())
                .orElseThrow(() -> new ApiException("Worker not found", HttpStatus.NOT_FOUND));
        AppUser admin = adminUserId == null ? null : appUserRepository.findById(adminUserId).orElse(null);

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new ApiException("Only pending requests can be assigned", HttpStatus.CONFLICT);
        }
        if (worker.getAvailability() != Availability.AVAILABLE) {
            throw new ApiException("Only available workers can be assigned", HttpStatus.CONFLICT);
        }

        Assignment assignment = new Assignment();
        assignment.setServiceRequest(request);
        assignment.setWorker(worker);
        assignment.setAssignedByAdmin(admin);
        assignment.setAssignmentStatus(AssignmentStatus.ASSIGNED);
        request.setStatus(RequestStatus.ASSIGNED);
        worker.setAvailability(Availability.BUSY);

        Assignment savedAssignment = assignmentRepository.save(assignment);
        activityService.record(request, "Worker Assigned", admin == null ? "Admin" : admin.getFullName());
        notificationService.notify(request.getUser(), "Worker assigned", worker.getUser().getFullName() + " has been assigned to your request.", NotificationType.WORKER_ASSIGNED);
        notificationService.notify(worker.getUser(), "New job assigned", "You have been assigned a " + request.getCategory() + " job.", NotificationType.WORKER_ASSIGNED);
        return toDto(savedAssignment);
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponseDto> getAssignments() {
        return assignmentRepository.findAllByOrderByAssignedAtDesc().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponseDto> getCompletionRequests() {
        return assignmentRepository.findByAssignmentStatusOrderByAssignedAtDesc(AssignmentStatus.COMPLETED_PENDING_VERIFICATION).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AssignmentResponseDto getAssignment(Long assignmentId) {
        return assignmentRepository.findWithDetailsById(assignmentId)
                .map(this::toDto)
                .orElseThrow(() -> new ApiException("Assignment not found", HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponseDto> getWorkerJobs(Long workerUserId) {
        return assignmentRepository.findByWorkerUserIdOrderByAssignedAtDesc(workerUserId).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponseDto> getWorkerCompletedJobs(Long workerUserId) {
        return assignmentRepository.findByWorkerUserIdAndAssignmentStatusOrderByAssignedAtDesc(workerUserId, AssignmentStatus.COMPLETED).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AssignmentResponseDto getWorkerJob(Long workerUserId, Long assignmentId) {
        return assignmentRepository.findByIdAndWorkerUserId(assignmentId, workerUserId)
                .map(this::toDto)
                .orElseThrow(() -> new ApiException("Job not found", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public AssignmentResponseDto updateWorkerJobStatus(Long workerUserId, Long assignmentId, AssignmentStatus status) {
        Assignment assignment = assignmentRepository.findByIdAndWorkerUserId(assignmentId, workerUserId)
                .orElseThrow(() -> new ApiException("Job not found", HttpStatus.NOT_FOUND));

        assignment.setAssignmentStatus(status);
        ServiceRequest request = assignment.getServiceRequest();
        WorkerProfile worker = assignment.getWorker();

        if (status == AssignmentStatus.ACCEPTED) {
            request.setStatus(RequestStatus.ASSIGNED);
            assignment.setAcceptedAt(LocalDateTime.now());
            worker.setAvailability(Availability.BUSY);
            activityService.record(request, "Worker Accepted", worker.getUser().getFullName());
            notificationService.notify(request.getUser(), "Worker accepted", worker.getUser().getFullName() + " accepted your request.", NotificationType.WORKER_ACCEPTED);
        } else if (status == AssignmentStatus.REJECTED) {
            request.setStatus(RequestStatus.PENDING);
            worker.setAvailability(Availability.AVAILABLE);
            assignment.setNotes("Worker rejected the assignment. Request returned to pending queue.");
            activityService.record(request, "Worker Rejected", worker.getUser().getFullName());
            notificationService.notify(request.getUser(), "Worker reassignment needed", "The assigned worker rejected the job. Your request is back in the pending queue.", NotificationType.SYSTEM);
        } else if (status == AssignmentStatus.IN_PROGRESS) {
            request.setStatus(RequestStatus.IN_PROGRESS);
            activityService.record(request, "Job Started", worker.getUser().getFullName());
            notificationService.notify(request.getUser(), "Job started", worker.getUser().getFullName() + " started work on your request.", NotificationType.JOB_STARTED);
        } else if (status == AssignmentStatus.COMPLETED_PENDING_VERIFICATION) {
            assignment.setCompletedAt(LocalDateTime.now());
            worker.setAvailability(Availability.BUSY);
            assignment.setNotes("Worker marked the job complete. Waiting for admin verification.");
            activityService.record(request, "Job Completed", worker.getUser().getFullName());
            notificationService.notify(request.getUser(), "Job completed", "Your job is waiting for admin verification.", NotificationType.JOB_COMPLETED);
        } else if (status == AssignmentStatus.COMPLETED) {
            request.setStatus(RequestStatus.COMPLETED);
            worker.setAvailability(Availability.AVAILABLE);
            worker.setCompletedJobs(worker.getCompletedJobs() + 1);
        }

        return toDto(assignmentRepository.save(assignment));
    }

    @Transactional
    public AssignmentResponseDto verifyCompletion(Long assignmentId) {
        Assignment assignment = assignmentRepository.findWithDetailsById(assignmentId)
                .orElseThrow(() -> new ApiException("Assignment not found", HttpStatus.NOT_FOUND));

        if (assignment.getAssignmentStatus() != AssignmentStatus.COMPLETED_PENDING_VERIFICATION) {
            throw new ApiException("Only jobs waiting verification can be verified", HttpStatus.CONFLICT);
        }

        ServiceRequest request = assignment.getServiceRequest();
        WorkerProfile worker = assignment.getWorker();
        assignment.setAssignmentStatus(AssignmentStatus.COMPLETED);
        request.setStatus(RequestStatus.COMPLETED);
        worker.setAvailability(Availability.AVAILABLE);
        worker.setCompletedJobs(worker.getCompletedJobs() + 1);
        assignment.setNotes("Completion verified by admin.");
        activityService.record(request, "Completion Verified", "Admin");
        notificationService.notify(request.getUser(), "Completion verified", "Your request is completed. You can now leave a review.", NotificationType.JOB_COMPLETED);
        notificationService.notify(worker.getUser(), "Completion verified", "Your completed job has been verified.", NotificationType.JOB_COMPLETED);

        return toDto(assignmentRepository.save(assignment));
    }

    public AssignmentResponseDto toDto(Assignment assignment) {
        AppUser admin = assignment.getAssignedByAdmin();
        return new AssignmentResponseDto(
                assignment.getId(),
                adminRequestService.toDto(assignment.getServiceRequest()),
                workerProfileService.toDto(assignment.getWorker()),
                admin == null ? null : new AdminUserSummaryDto(admin.getId(), admin.getFullName(), admin.getEmail(), admin.getPhone(), admin.getProfilePicture(), admin.getAddress(), admin.getCity()),
                assignment.getAssignedAt(),
                assignment.getAcceptedAt(),
                assignment.getCompletedAt(),
                assignment.getNotes(),
                assignment.getAssignmentStatus()
        );
    }
}
