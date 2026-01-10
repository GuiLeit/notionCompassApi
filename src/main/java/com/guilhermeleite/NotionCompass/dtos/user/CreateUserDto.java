package com.guilhermeleite.NotionCompass.dtos.user;

import jakarta.validation.constraints.NotBlank;

public record CreateUserDto(
        @NotBlank(message = "User ID cannot be null or empty")
        String notionId,
        String type
) {}
