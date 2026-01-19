package com.guilhermeleite.NotionCompass.services;

import com.guilhermeleite.NotionCompass.config.NotionProperties;
import com.guilhermeleite.NotionCompass.domains.workspace.Workspace;
import com.guilhermeleite.NotionCompass.domains.workspace.exceptions.WorkspaceRequestException;
import com.guilhermeleite.NotionCompass.dtos.notion.NotionWorkspaceResponseDto;
import com.guilhermeleite.NotionCompass.dtos.page.PageDetailsDto;
import com.guilhermeleite.NotionCompass.dtos.workspace.*;
import com.guilhermeleite.NotionCompass.repositories.WorkspaceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

import java.util.*;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final NotionProperties notionProperties;
    private final RestTemplate restTemplate;
    private final WorkspaceRepository workspaceRepository;
    private final PagesService pagesService;

    private WorkspaceDetailsDto mapToDetailsDto(Workspace workspace) {
        return new WorkspaceDetailsDto(
                workspace.getId(),
                workspace.getName(),
                workspace.getIcon()
        );
    }

    public Optional<Workspace> findById(String id) {
        return this.workspaceRepository.findById(id);
    }

    public Optional<Workspace> findByNotionId(String notionId) {
        return this.workspaceRepository.findByNotionWorkspaceId(notionId);
    }

    public List<Workspace> findByUserId(Long userId) {
        return this.workspaceRepository.findByUserId(userId);
    }

    public Workspace createOrUpdate(CreateWorkspaceDto workspaceDto){
        Workspace workspace = this.findByNotionId(workspaceDto.notionId())
                .orElse(new Workspace());

        workspace.setNotionWorkspaceId(workspaceDto.notionId());
        workspace.setUser(workspaceDto.user());
        workspace.setAccessToken(workspaceDto.accessToken());
        workspace.setName(workspaceDto.name());
        workspace.setIcon(workspaceDto.icon());
        return workspaceRepository.save(workspace);
    }

    // Dto methods
    public WorkspaceDetailsDto getWorkspaceById(String id) {
        Workspace workspace = this.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Workspace not found"));

        return this.mapToDetailsDto(workspace);
    }

    public WorkspaceDetailsListDto getWorkspacesByUserId(Long userId) {
        List<Workspace> workspaces = this.findByUserId(userId);
        List<WorkspaceDetailsDto> workspaceDetailsDtos = workspaces.stream()
                .map(this::mapToDetailsDto)
                .toList();

        return new WorkspaceDetailsListDto(workspaceDetailsDtos);
    }

    public WorkspaceDetailsWithPagesListDto getWorkspacesWithPagesByUserId(Long userId) {
        List<Workspace> workspaces = this.findByUserId(userId);
        List<WorkspaceDetailsWithPagesDto> workspacesDtoList = new ArrayList<>();
        for (Workspace workspace : workspaces) {
            workspacesDtoList.add(new WorkspaceDetailsWithPagesDto(
                    workspace.getId(),
                    workspace.getName(),
                    workspace.getIcon(),
                    this.pagesService.getPagesByWorkspaceId(workspace.getId())
            ));
        }

        return new WorkspaceDetailsWithPagesListDto(workspacesDtoList);
    }

    public List<PageDetailsDto> getWorkspacePagesById(String id) {
        Workspace workspace = this.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Workspace not found"));

        return this.pagesService.getPagesFromNotionApi(workspace);
    }

    public NotionWorkspaceResponseDto exchangeCodeForWorkspaceData(String code) {
        String credentials = String.format("%s:%s", notionProperties.getClientId(), notionProperties.getClientSecret());
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        //Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Notion-Version", "2022-06-28");
        headers.add("Authorization", "Basic " + encodedCredentials);

        //Body
        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "authorization_code");
        body.put("code", code);
        body.put("redirect_uri", this.notionProperties.getCallbackUri().toString());

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            return restTemplate.postForObject(
                    notionProperties.getTokenUrl(),
                    request,
                    NotionWorkspaceResponseDto.class
            );
        } catch (HttpClientErrorException e) {
            throw new WorkspaceRequestException("Invalid authorization code or client credentials: " + e.getStatusCode(), e);
        } catch (HttpServerErrorException e) {
            throw new WorkspaceRequestException("Notion API server error: " + e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            throw new WorkspaceRequestException("Failed to connect to Notion API", e);
        } catch (RestClientException e) {
            throw new WorkspaceRequestException("Invalid authorization code: " + e.getLocalizedMessage(), e);
        }
    }
}
