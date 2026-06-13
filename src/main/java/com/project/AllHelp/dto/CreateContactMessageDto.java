package com.project.AllHelp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateContactMessageDto(
        @NotBlank @Size(max = 120) String fullName,
        @NotBlank @Size(max = 30) String phone,
        @NotBlank @Email @Size(max = 160) String email,
        @NotBlank @Size(max = 2000) String message
) {
}
