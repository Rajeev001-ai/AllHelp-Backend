package com.project.AllHelp.service;

import com.project.AllHelp.dto.NotificationDto;
import com.project.AllHelp.entity.AppUser;
import com.project.AllHelp.entity.Notification;
import com.project.AllHelp.entity.NotificationType;
import com.project.AllHelp.exception.ApiException;
import com.project.AllHelp.repository.NotificationRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void notify(AppUser user, String title, String message, NotificationType type) {
        if (user == null || user.getId() == null) {
            return;
        }
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toDto).toList();
    }

    @Transactional
    public NotificationDto markRead(Long userId, Long id) {
        Notification notification = notificationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ApiException("Notification not found", HttpStatus.NOT_FOUND));
        notification.setIsRead(true);
        return toDto(notificationRepository.save(notification));
    }

    @Transactional
    public void markAllRead(Long userId) {
        notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).forEach(notification -> notification.setIsRead(true));
    }

    @Transactional(readOnly = true)
    public long unreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    private NotificationDto toDto(Notification notification) {
        return new NotificationDto(notification.getId(), notification.getTitle(), notification.getMessage(), notification.getType(), notification.getIsRead(), notification.getCreatedAt());
    }
}
