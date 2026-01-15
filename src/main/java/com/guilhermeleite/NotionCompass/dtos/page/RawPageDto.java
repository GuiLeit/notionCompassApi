package com.guilhermeleite.NotionCompass.dtos.page;

public record RawPageDto(
        String notionPageId,
        String parentId,
        String title,
        String icon,
        String url
) {
}
