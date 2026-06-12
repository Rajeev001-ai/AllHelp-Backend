package com.project.AllHelp.controller;

import com.project.AllHelp.dto.CreateRequestDto;
import com.project.AllHelp.dto.RequestResponseDto;
import com.project.AllHelp.security.UserPrincipal;
import com.project.AllHelp.service.ServiceRequestService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/requests")
@PreAuthorize("hasRole('USER')")
public class UserRequestController {

    private final ServiceRequestService serviceRequestService;

    public UserRequestController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    @PostMapping
    public ResponseEntity<RequestResponseDto> createRequest(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateRequestDto request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceRequestService.createRequest(principal.getId(), request));
    }

    @GetMapping
    public List<RequestResponseDto> getRequests(@AuthenticationPrincipal UserPrincipal principal) {
        return serviceRequestService.getUserRequests(principal.getId());
    }

    @GetMapping("/{id}")
    public RequestResponseDto getRequest(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return serviceRequestService.getUserRequest(principal.getId(), id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        serviceRequestService.deletePendingRequest(principal.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
