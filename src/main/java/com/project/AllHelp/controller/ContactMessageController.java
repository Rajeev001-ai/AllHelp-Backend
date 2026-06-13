package com.project.AllHelp.controller;

import com.project.AllHelp.dto.ContactMessageDto;
import com.project.AllHelp.dto.CreateContactMessageDto;
import com.project.AllHelp.service.ContactMessageService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContactMessageController {

    private final ContactMessageService contactMessageService;

    public ContactMessageController(ContactMessageService contactMessageService) {
        this.contactMessageService = contactMessageService;
    }

    @PostMapping("/api/contact")
    public ResponseEntity<ContactMessageDto> create(@Valid @RequestBody CreateContactMessageDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contactMessageService.create(dto));
    }

    @GetMapping("/api/admin/contact-messages")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ContactMessageDto> getMessages() {
        return contactMessageService.getAll();
    }

    @GetMapping("/api/admin/contact-messages/unread-count")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Long> getUnreadCount() {
        return Map.of("count", contactMessageService.getUnreadCount());
    }

    @PatchMapping("/api/admin/contact-messages/{id}/read")
    @PreAuthorize("hasRole('ADMIN')")
    public ContactMessageDto markRead(@PathVariable Long id) {
        return contactMessageService.markRead(id);
    }
}
