package com.guilhermeleite.NotionCompass.dtos.workspace;

import java.util.List;

public record WorkspaceListResponseDto(
        List<WorkspaceResponseDto> workspaces
) {
}
