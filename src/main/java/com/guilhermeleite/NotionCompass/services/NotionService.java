package com.guilhermeleite.NotionCompass.services;

import com.guilhermeleite.NotionCompass.config.NotionProperties;
import com.guilhermeleite.NotionCompass.domains.user.User;
import com.guilhermeleite.NotionCompass.domains.workspace.exceptions.WorkspaceRequestException;
import com.guilhermeleite.NotionCompass.dtos.NotionWorkspaceResponseDto;
import com.guilhermeleite.NotionCompass.dtos.user.CreateUserDto;
import com.guilhermeleite.NotionCompass.dtos.workspace.CreateWorkspaceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

import java.net.URI;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotionService {

    private final NotionProperties notionProperties;
    private final RestTemplate restTemplate;
    private final UserService userService;
    private final WorkspaceService workspaceService;

    public static URI getCallbackUri() {
        return URI.create("https://zealous-aurora-13.webhook.cool");
//        return ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/api/auth/notion/callback")
//                .build()
//                .toUri();
    }

    public URI buildAuthorizationUri() {
        String uriString = String.format("%s?client_id=%s&response_type=code&owner=user&redirect_uri=%s",
                notionProperties.getAuthorizationUrl(),
                notionProperties.getClientId(),
                NotionService.getCallbackUri().toString());
        return URI.create(uriString);
    }

    public void handleOauthCallback(String code) {
        NotionWorkspaceResponseDto rawWorkpsace = this.exchangeCodeForWorkspaceData(code);
        User user = userService.findOrCreateUser(new CreateUserDto(
                rawWorkpsace.getOwner().getUser().getId(),
                rawWorkpsace.getOwner().getType()
        ));

        Map<String, Object> pages = this.fetchWorkspacePages(rawWorkpsace.getAccessToken());
        workspaceService.createOrUpdateWorkspace(new CreateWorkspaceDto(
                rawWorkpsace.getWorkspaceId(),
                user,
                rawWorkpsace.getAccessToken(),
                rawWorkpsace.getWorkspaceName(),
                rawWorkpsace.getWorkspaceIcon(),
                pages
        ));
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
        body.put("redirect_uri", NotionService.getCallbackUri().toString());

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

    public Map<String, Object> fetchWorkspacePages(String workspaceAccessToken) {
        //Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Notion-Version", "2022-06-28");
        headers.add("Authorization", workspaceAccessToken);

        Map<String, Object> body = new HashMap<>();
        body.put("sort", Map.of(
                "direction", "ascending",
                "timestamp", "created_time"
        ));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            return restTemplate.postForObject(
                    this.notionProperties.getPagesUrl(),
                    request,
                    Map.class
            );
        } catch (HttpClientErrorException e) {
            throw new WorkspaceRequestException("Invalid access token or client credentials: " + e.getStatusCode(), e);
        } catch (HttpServerErrorException e) {
            throw new WorkspaceRequestException("Notion API server error: " + e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            throw new WorkspaceRequestException("Failed to connect to Notion API", e);
        } catch (RestClientException e) {
            throw new WorkspaceRequestException("Invalid access token: " + e.getLocalizedMessage(), e);
        }
    }
}
