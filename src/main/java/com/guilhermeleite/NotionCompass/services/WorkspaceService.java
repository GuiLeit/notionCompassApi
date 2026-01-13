package com.guilhermeleite.NotionCompass.services;

import com.guilhermeleite.NotionCompass.domains.workspace.Workspace;
import com.guilhermeleite.NotionCompass.dtos.workspace.CreateWorkspaceDto;
import com.guilhermeleite.NotionCompass.repositories.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    public Iterable<Workspace> findAll() {
        return workspaceRepository.findAll();
    }

    public Optional<Workspace> findById(Long id) {
        return workspaceRepository.findById(id);
    }

    public Optional<Workspace> findByNotionId(String notionId) {
        return workspaceRepository.findByNotionWorkspaceId(notionId);
    }

    public Workspace createWorkspace(CreateWorkspaceDto createWorkspaceDto) {
        Workspace workspace = new Workspace();
        workspace.setNotionWorkspaceId(createWorkspaceDto.notionId());
        workspace.setUser(createWorkspaceDto.user());
        workspace.setAccessToken(createWorkspaceDto.accessToken());
        workspace.setName(createWorkspaceDto.name());
        workspace.setIcon(createWorkspaceDto.icon());
        workspace.setPages(createWorkspaceDto.pages());
        return workspaceRepository.save(workspace);
    }

    public Workspace createOrUpdateWorkspace(CreateWorkspaceDto workspaceDto){
        Workspace workspace = this.findByNotionId(workspaceDto.notionId())
                .orElse(new Workspace());

        workspace.setNotionWorkspaceId(workspaceDto.notionId());
        workspace.setUser(workspaceDto.user());
        workspace.setAccessToken(workspaceDto.accessToken());
        workspace.setName(workspaceDto.name());
        workspace.setIcon(workspaceDto.icon());
        workspace.setPages(workspaceDto.pages());
        return workspaceRepository.save(workspace);
    }

}
