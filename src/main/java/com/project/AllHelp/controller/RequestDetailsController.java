package com.project.AllHelp.controller;

import com.project.AllHelp.dto.RequestDetailsDto;
import com.project.AllHelp.security.UserPrincipal;
import com.project.AllHelp.service.RequestDetailsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/requests")
public class RequestDetailsController {
    private final RequestDetailsService requestDetailsService;

    public RequestDetailsController(RequestDetailsService requestDetailsService) {
        this.requestDetailsService = requestDetailsService;
    }

    @GetMapping("/{id}/details")
    public RequestDetailsDto getDetails(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return requestDetailsService.getDetails(principal.getId(), principal.getRole(), id);
    }
}
