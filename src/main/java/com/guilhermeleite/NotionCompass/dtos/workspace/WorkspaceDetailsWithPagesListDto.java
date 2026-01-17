package com.guilhermeleite.NotionCompass.dtos.workspace;

import java.util.List;

public record WorkspaceDetailsWithPagesListDto(
        List<WorkspaceDetailsWithPagesDto> workspaces
) {
}
