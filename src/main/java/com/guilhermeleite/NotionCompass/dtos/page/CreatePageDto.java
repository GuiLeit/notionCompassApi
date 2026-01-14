package com.guilhermeleite.NotionCompass.dtos.page;

import com.guilhermeleite.NotionCompass.domains.workspace.Workspace;

import java.util.Map;

public record CreatePageDto(
        Workspace workspace,
        Map<String, Object> object
) {
}
