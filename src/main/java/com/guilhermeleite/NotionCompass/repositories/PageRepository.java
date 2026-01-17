package com.guilhermeleite.NotionCompass.repositories;

import com.guilhermeleite.NotionCompass.domains.page.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PageRepository extends JpaRepository<Page, Long> {
    List<Page> findByWorkspaceId(String workspaceId);
    Optional<Page> findByNotionPageId(String notionPageId);
}
