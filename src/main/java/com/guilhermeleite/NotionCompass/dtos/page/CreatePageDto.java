package com.guilhermeleite.NotionCompass.dtos.page;

import com.guilhermeleite.NotionCompass.domains.workspace.Workspace;
import tools.jackson.databind.JsonNode;

import java.util.Map;

public record CreatePageDto(
        Workspace workspace,
        String notionPageId,
        String parentId,
        String title,
        String icon,
        String url
) {
}
