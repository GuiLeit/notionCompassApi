package com.guilhermeleite.NotionCompass.dtos.workspace;

import com.guilhermeleite.NotionCompass.domains.user.User;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record CreateWorkspaceDto(
        @NotBlank(message = "User ID cannot be null or empty")
        String notionId,
        User user,
        String accessToken,
        String name,
        String icon,
        Map<String, Object> pages
) {}
