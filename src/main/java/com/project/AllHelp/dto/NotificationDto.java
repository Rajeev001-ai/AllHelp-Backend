package com.project.AllHelp.dto;

import com.project.AllHelp.entity.NotificationType;
import java.time.LocalDateTime;

public record NotificationDto(
        Long id,
        String title,
        String message,
        NotificationType type,
        Boolean isRead,
        LocalDateTime createdAt
) {
}
