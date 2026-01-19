package com.guilhermeleite.NotionCompass.services;

import com.guilhermeleite.NotionCompass.config.ExceptionEntityHandler;
import com.guilhermeleite.NotionCompass.config.NotionProperties;
import com.guilhermeleite.NotionCompass.domains.page.Page;
import com.guilhermeleite.NotionCompass.domains.workspace.Workspace;
import com.guilhermeleite.NotionCompass.domains.workspace.exceptions.PagesRequestException;
import com.guilhermeleite.NotionCompass.dtos.page.CreatePageDto;
import com.guilhermeleite.NotionCompass.dtos.page.PageDetailsDto;
import com.guilhermeleite.NotionCompass.dtos.page.RawPageDto;
import com.guilhermeleite.NotionCompass.repositories.PageRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;
import tools.jackson.databind.JsonNode;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PagesService {

    private final PageRepository pageRepository;
    private final NotionProperties notionProperties;
    private final RestTemplate restTemplate;
    private static final Logger log = LoggerFactory.getLogger(ExceptionEntityHandler.class);

    private Optional<RawPageDto> mapPageObjectToDto(JsonNode pageObj) {
        try {
            return Optional.of(new RawPageDto(
                    pageObj.path("id").asString(),
                    pageObj.at("/parent/page_id").asString(null),
                    this.extractTitle(pageObj),
                    this.extractIcon(pageObj),
                    pageObj.path("url").asString()
            ));
        } catch (Exception e){
            log.error("Error mapping page object to DTO: {}\n From page object: {}", e.getMessage(), pageObj);
            return Optional.empty();
        }
    }

    private String extractTitle(JsonNode pageObj) {
        var title = pageObj.at("/properties/title/title/0/text/content").asString(null);
        if(title == null){
            title = pageObj.at("/properties/title/title/0/plain_text").asString(null);
        }
        return title;
    }
    private String extractIcon(JsonNode pageObj) {
        var icon = pageObj.at("/icon/emoji").asString(null);
        if(icon == null){
            icon = pageObj.at("/icon/external/url").asString(null);
        }
        return icon;
    }

    public Optional<Page> findByNotionId(String notionId) {
        return this.pageRepository.findByNotionPageId(notionId);
    }

    public Page create(CreatePageDto pageDto) {
        Page page = new Page();
        page.setWorkspace(pageDto.workspace());
        page.setNotionPageId(pageDto.notionPageId());
        page.setNotionParentPageId(pageDto.parentId());
        page.setTitle(pageDto.title());
        page.setIcon(pageDto.icon());
        page.setUrl(pageDto.url());
        return this.pageRepository.save(page);
    }

    public Page createOrUpdate(CreatePageDto pageDto) {
        Page page = this.findByNotionId(pageDto.notionPageId())
                .orElse(new Page());

        page.setWorkspace(pageDto.workspace());
        page.setNotionPageId(pageDto.notionPageId());
        page.setNotionParentPageId(pageDto.parentId());
        page.setTitle(pageDto.title());
        page.setIcon(pageDto.icon());
        page.setUrl(pageDto.url());
        return this.pageRepository.save(page);
    }

    public List<PageDetailsDto> getPagesByWorkspaceId(String workspaceId) {
        return this.pageRepository.findByWorkspaceId(workspaceId).stream()
                .map(page -> new PageDetailsDto(
                        page.getWorkspace().getNotionWorkspaceId(),
                        page.getNotionPageId(),
                        page.getNotionParentPageId(),
                        page.getTitle(),
                        page.getIcon(),
                        page.getUrl()
                ))
                .toList();
    }

    public List<PageDetailsDto> getPagesFromNotionApi(Workspace workspace) {
        JsonNode response = this.fetchPages(workspace.getAccessToken());
        List<PageDetailsDto> pages = new ArrayList<>();

        for (JsonNode pageObj : (Iterable<JsonNode>) response.get("results")) {
            this.mapPageObjectToDto(pageObj).ifPresent(pageDto -> {
                this.createOrUpdate(new CreatePageDto(
                        workspace,
                        pageDto.notionPageId(),
                        pageDto.parentId(),
                        pageDto.title(),
                        pageDto.icon(),
                        pageDto.url()
                ));
                pages.add(new PageDetailsDto(
                        workspace.getNotionWorkspaceId(),
                        pageDto.notionPageId(),
                        pageDto.parentId(),
                        pageDto.title(),
                        pageDto.icon(),
                        pageDto.url()
                ));
            });

        }

        return pages;
    }

    public Page getPageFromNotionApi(Workspace workspace, String pageId) {
        JsonNode response = this.fetchPageById(workspace.getAccessToken(), pageId);
        RawPageDto pageDto = this.mapPageObjectToDto(response)
                .orElse(null);

        if(pageDto == null) {
            return null;
        }

        return this.createOrUpdate(new CreatePageDto(
                workspace,
                pageDto.notionPageId(),
                pageDto.parentId(),
                pageDto.title(),
                pageDto.icon(),
                pageDto.url()
        ));
    }

    private JsonNode fetchPages(String workspaceAccessToken) {
        //Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Notion-Version", "2022-06-28");
        headers.add("Authorization", workspaceAccessToken);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(new HashMap<>(), headers);

        try {
            return restTemplate.postForObject(
                    this.notionProperties.getPagesUrl(),
                    request,
                    JsonNode.class
            );
        } catch (HttpClientErrorException e) {
            throw new PagesRequestException("Invalid access token or client credentials: " + e.getStatusCode(), e);
        } catch (HttpServerErrorException e) {
            throw new PagesRequestException("Notion API server error: " + e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            throw new PagesRequestException("Failed to connect to Notion API", e);
        } catch (RestClientException e) {
            throw new PagesRequestException("Invalid access token: " + e.getLocalizedMessage(), e);
        }
    }

    private JsonNode fetchPageById(String workspaceAccessToken, String pageId) {
        //Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Notion-Version", "2022-06-28");
        headers.add("Authorization", workspaceAccessToken);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(new HashMap<>(), headers);

        try {
            return restTemplate.postForObject(
                    this.notionProperties.getBaseNotionRoute() + "/pages/" + pageId,
                    request,
                    JsonNode.class
            );
        } catch (HttpClientErrorException e) {
            throw new PagesRequestException("Invalid access token or client credentials: " + e.getStatusCode(), e);
        } catch (HttpServerErrorException e) {
            throw new PagesRequestException("Notion API server error: " + e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            throw new PagesRequestException("Failed to connect to Notion API", e);
        } catch (RestClientException e) {
            throw new PagesRequestException("Invalid access token: " + e.getLocalizedMessage(), e);
        }
    }
}
