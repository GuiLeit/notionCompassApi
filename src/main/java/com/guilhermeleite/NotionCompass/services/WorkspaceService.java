package com.guilhermeleite.NotionCompass.services;

import com.guilhermeleite.NotionCompass.domains.workspace.Workspace;
import com.guilhermeleite.NotionCompass.dtos.workspace.CreateWorkspaceDto;
import com.guilhermeleite.NotionCompass.dtos.workspace.WorkspaceDetailsDto;
import com.guilhermeleite.NotionCompass.dtos.workspace.WorkspaceDetailsListDto;
import com.guilhermeleite.NotionCompass.repositories.WorkspaceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final PagesService pagesService;

    private WorkspaceDetailsDto mapToDetailsDto(Workspace workspace) {
        return new WorkspaceDetailsDto(
                workspace.getNotionWorkspaceId(),
                workspace.getName(),
                workspace.getIcon()
        );
    }

    public Optional<Workspace> findByNotionId(String notionId) {
        return this.workspaceRepository.findByNotionWorkspaceId(notionId);
    }

    public Workspace createOrUpdate(CreateWorkspaceDto workspaceDto){
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

    // Dto methods
    public WorkspaceDetailsListDto getWorkspaces() {
        List<Workspace> workspaces = workspaceRepository.findAll();

        List<WorkspaceDetailsDto> workspaceDetailsDtoList = workspaces.stream()
                .map(this::mapToDetailsDto)
                .toList();

        return new WorkspaceDetailsListDto(workspaceDetailsDtoList);
    }

    public WorkspaceDetailsDto getWorkspaceByNotionId(String notionId) {
        Workspace workspace = this.findByNotionId(notionId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace not found"));

        return this.mapToDetailsDto(workspace);
    }

    public Map<String, Object> getWorkspacePagesByNotionId(String notionId) {
        Workspace workspace = this.findByNotionId(notionId)
                .orElseThrow(() -> new EntityNotFoundException("Workspace not found"));

        return pagesService.fetchPagesByWorkspaceToken(workspace.getAccessToken());
    }
}
