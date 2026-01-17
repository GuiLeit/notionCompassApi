package com.guilhermeleite.NotionCompass.repositories;

import com.guilhermeleite.NotionCompass.domains.workspace.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkspaceRepository extends JpaRepository<Workspace, String> {
    Optional<Workspace> findByNotionWorkspaceId(String notionWorkspaceId);
    List<Workspace> findByUserId(Long userId);
}
