package com.guilhermeleite.NotionCompass.dtos.workspace;

import com.guilhermeleite.NotionCompass.dtos.page.PageDetailsDto;

import java.time.LocalDateTime;
import java.util.List;

public record WorkspaceDetailsWithPagesDto(
        String id,
        String name,
        String icon,
        LocalDateTime updatedAt,
        List<PageDetailsDto> pages
) {
}
