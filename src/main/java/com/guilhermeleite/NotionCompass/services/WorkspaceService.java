package com.guilhermeleite.NotionCompass.services;

import com.guilhermeleite.NotionCompass.domains.workspace.Workspace;
import com.guilhermeleite.NotionCompass.dtos.workspace.CreateWorkspaceDto;
import com.guilhermeleite.NotionCompass.dtos.workspace.WorkspaceListResponseDto;
import com.guilhermeleite.NotionCompass.dtos.workspace.WorkspaceResponseDto;
import com.guilhermeleite.NotionCompass.repositories.WorkspaceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceListResponseDto findAll() {
        List<Workspace> workspaces = workspaceRepository.findAll();

        List<WorkspaceResponseDto> workspaceResponseDtoList = workspaces.stream()
                .map(workspace -> new WorkspaceResponseDto(
                        workspace.getNotionWorkspaceId(),
                        workspace.getName(),
                        workspace.getIcon()
                ))
                .toList();

        return new WorkspaceListResponseDto(workspaceResponseDtoList);
    }

    public WorkspaceResponseDto findByNotionId(String notionId) {
        Workspace workspace = workspaceRepository.findByNotionWorkspaceId(notionId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace not found"));

        return new WorkspaceResponseDto(
                workspace.getNotionWorkspaceId(),
                workspace.getName(),
                workspace.getIcon()
        );
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
