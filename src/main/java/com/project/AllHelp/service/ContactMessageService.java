package com.project.AllHelp.service;

import com.project.AllHelp.dto.ContactMessageDto;
import com.project.AllHelp.dto.CreateContactMessageDto;
import com.project.AllHelp.entity.ContactMessage;
import com.project.AllHelp.exception.ApiException;
import com.project.AllHelp.repository.ContactMessageRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;

    public ContactMessageService(ContactMessageRepository contactMessageRepository) {
        this.contactMessageRepository = contactMessageRepository;
    }

    @Transactional
    public ContactMessageDto create(CreateContactMessageDto dto) {
        ContactMessage message = new ContactMessage();
        message.setFullName(dto.fullName().trim());
        message.setPhone(dto.phone().trim());
        message.setEmail(dto.email().trim().toLowerCase());
        message.setMessage(dto.message().trim());

        return toDto(contactMessageRepository.save(message));
    }

    @Transactional(readOnly = true)
    public List<ContactMessageDto> getAll() {
        return contactMessageRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public long getUnreadCount() {
        return contactMessageRepository.countByReadFalse();
    }

    @Transactional
    public ContactMessageDto markRead(Long id) {
        ContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new ApiException("Contact message not found", HttpStatus.NOT_FOUND));
        message.setRead(true);
        return toDto(contactMessageRepository.save(message));
    }

    private ContactMessageDto toDto(ContactMessage message) {
        return new ContactMessageDto(
                message.getId(),
                message.getFullName(),
                message.getPhone(),
                message.getEmail(),
                message.getMessage(),
                message.isRead(),
                message.getCreatedAt()
        );
    }
}
