package com.guilhermeleite.NotionCompass.dtos.workspace;

import java.time.LocalDateTime;

public record WorkspaceDetailsDto(
        String id,
        String name,
        String icon,
        LocalDateTime updatedAt
) {
}
