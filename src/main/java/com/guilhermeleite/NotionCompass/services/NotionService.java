package com.guilhermeleite.NotionCompass.services;

import com.guilhermeleite.NotionCompass.config.NotionProperties;
import com.guilhermeleite.NotionCompass.domains.user.User;
import com.guilhermeleite.NotionCompass.dtos.NotionWorkspaceResponseDto;
import com.guilhermeleite.NotionCompass.dtos.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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

    public static URI getCallbackUri() {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/auth/notion/callback")
                .build()
                .toUri();
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
        User user = userService.findOrCreateUser(new UserDto(
                rawWorkpsace.getOwner().getUser().getId(),
                null
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
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to exchange code for token", e);
        }

    }
}
