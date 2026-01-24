package com.guilhermeleite.NotionCompass.dtos.page;

public record PageDetailsDto(
        String workspaceId,
        String id,
        String parentId,
        String title,
        String icon,
        String url
) {
}
