package com.guilhermeleite.NotionCompass.dtos.workspace;

import com.guilhermeleite.NotionCompass.dtos.page.PageDetailsDto;

import java.util.List;

public record WorkspaceDetailsWithPagesDto(
        String id,
        String name,
        String icon,
        List<PageDetailsDto> pages
) {
}
