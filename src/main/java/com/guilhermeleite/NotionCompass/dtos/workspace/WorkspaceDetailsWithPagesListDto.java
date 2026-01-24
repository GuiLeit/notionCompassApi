package com.guilhermeleite.NotionCompass.dtos.workspace;

import java.time.LocalDateTime;
import java.util.List;

public record WorkspaceDetailsWithPagesListDto(
        LocalDateTime lastUpdate,
        List<WorkspaceDetailsWithPagesDto> workspaces
) {
}
