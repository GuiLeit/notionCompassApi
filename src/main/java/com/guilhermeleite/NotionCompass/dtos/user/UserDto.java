package com.guilhermeleite.NotionCompass.dtos.user;

import jakarta.validation.constraints.NotBlank;

public record UserDto(
        @NotBlank(message = "User ID cannot be null or empty")
        String id,
        String username
) {}
