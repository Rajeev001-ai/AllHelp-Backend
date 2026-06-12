package com.project.AllHelp.dto;

import com.project.AllHelp.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank String fullName,
        @Email @NotBlank String email,
        @NotBlank String phone,
        @Size(min = 6, message = "Password must be at least 6 characters") String password,
        @NotNull Role role
) {
}
