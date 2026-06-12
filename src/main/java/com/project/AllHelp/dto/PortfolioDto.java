package com.project.AllHelp.dto;

import java.time.LocalDateTime;

public record PortfolioDto(
        Long id,
        String imageUrl,
        String title,
        String description,
        LocalDateTime createdAt
) {
}
