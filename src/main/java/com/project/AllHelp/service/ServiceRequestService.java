package com.project.AllHelp.service;

import com.project.AllHelp.dto.CreateRequestDto;
import com.project.AllHelp.dto.RequestResponseDto;
import com.project.AllHelp.entity.AppUser;
import com.project.AllHelp.entity.NotificationType;
import com.project.AllHelp.entity.RequestStatus;
import com.project.AllHelp.entity.ServiceRequest;
import com.project.AllHelp.entity.Urgency;
import com.project.AllHelp.exception.ApiException;
import com.project.AllHelp.repository.AppUserRepository;
import com.project.AllHelp.repository.ServiceRequestRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final AppUserRepository appUserRepository;
    private final NotificationService notificationService;
    private final ActivityService activityService;

    public ServiceRequestService(
            ServiceRequestRepository serviceRequestRepository,
            AppUserRepository appUserRepository,
            NotificationService notificationService,
            ActivityService activityService
    ) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.appUserRepository = appUserRepository;
        this.notificationService = notificationService;
        this.activityService = activityService;
    }

    @Transactional
    public RequestResponseDto createRequest(Long userId, CreateRequestDto dto) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        ServiceRequest request = new ServiceRequest();
        request.setUser(user);
        request.setCategory(dto.category());
        request.setDescription(dto.description());
        request.setAddress(dto.address());
        request.setCity(dto.city());
        request.setPreferredDate(dto.preferredDate());
        request.setPreferredTime(dto.preferredTime());
        request.setUrgency(dto.urgency() == null ? Urgency.MEDIUM : dto.urgency());
        request.setStatus(RequestStatus.PENDING);

        ServiceRequest savedRequest = serviceRequestRepository.save(request);
        activityService.record(savedRequest, "Request Created", user.getFullName());
        notificationService.notify(user, "Request created", "Your " + savedRequest.getCategory() + " request was created successfully.", NotificationType.REQUEST_CREATED);
        return toDto(savedRequest);
    }

    @Transactional(readOnly = true)
    public List<RequestResponseDto> getUserRequests(Long userId) {
        return serviceRequestRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public RequestResponseDto getUserRequest(Long userId, Long requestId) {
        return serviceRequestRepository.findByIdAndUserId(requestId, userId)
                .map(this::toDto)
                .orElseThrow(() -> new ApiException("Request not found", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public void deletePendingRequest(Long userId, Long requestId) {
        ServiceRequest request = serviceRequestRepository.findByIdAndUserId(requestId, userId)
                .orElseThrow(() -> new ApiException("Request not found", HttpStatus.NOT_FOUND));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new ApiException("Only pending requests can be deleted", HttpStatus.BAD_REQUEST);
        }

        serviceRequestRepository.delete(request);
    }

    public RequestResponseDto toDto(ServiceRequest request) {
        return new RequestResponseDto(
                request.getId(),
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
}
