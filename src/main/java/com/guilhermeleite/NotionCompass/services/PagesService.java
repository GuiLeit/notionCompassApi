package com.guilhermeleite.NotionCompass.services;

import com.guilhermeleite.NotionCompass.config.NotionProperties;
import com.guilhermeleite.NotionCompass.domains.page.Page;
import com.guilhermeleite.NotionCompass.domains.workspace.Workspace;
import com.guilhermeleite.NotionCompass.domains.workspace.exceptions.PagesRequestException;
import com.guilhermeleite.NotionCompass.dtos.page.CreatePageDto;
import com.guilhermeleite.NotionCompass.repositories.PageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PagesService {

    private final PageRepository pageRepository;
    private final NotionProperties notionProperties;
    private final RestTemplate restTemplate;

    public Page create(CreatePageDto pageDto) {
        Page page = new Page();
        page.setWorkspace(pageDto.workspace());
        page.setObject(pageDto.object());
        return this.pageRepository.save(page);
    }

    @SuppressWarnings("unchecked")
    public List<Object> getPagesByWorkspaceToken(Workspace workspace) {
        Map<String, Object> response = this.fetchPages(workspace.getAccessToken());
        List<Object> pages = new ArrayList<>();

        for (Object pageObj : (Iterable<?>) response.get("results")) {
            Map<String, Object> pageMap = (Map<String, Object>) pageObj;
            this.create(new CreatePageDto(
                    workspace,
                    pageMap
            ));
            pages.add(pageMap);
        }

        return pages;
    }

    private Map<String, Object> fetchPages(String workspaceAccessToken) {
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
                    Map.class
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
