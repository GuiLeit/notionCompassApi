package com.guilhermeleite.NotionCompass.services;

import com.guilhermeleite.NotionCompass.config.NotionProperties;
import com.guilhermeleite.NotionCompass.domains.user.User;
import com.guilhermeleite.NotionCompass.domains.workspace.Workspace;
import com.guilhermeleite.NotionCompass.domains.workspace.exceptions.PagesRequestException;
import com.guilhermeleite.NotionCompass.domains.workspace.exceptions.WorkspaceRequestException;
import com.guilhermeleite.NotionCompass.dtos.AuthTokenDetailsDto;
import com.guilhermeleite.NotionCompass.dtos.NotionWorkspaceResponseDto;
import com.guilhermeleite.NotionCompass.dtos.user.CreateUserDto;
import com.guilhermeleite.NotionCompass.dtos.workspace.CreateWorkspaceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    private final PagesService pagesService;

    @Value("${app.extension.id}")
    private String extentionId;

    public static URI getCallbackUri() {
        return URI.create("https://jolly-rain-32.webhook.cool");
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

    public URI buildSuccessAuthorizationUri(AuthTokenDetailsDto tokenDto) {
        String uriString = String.format("chrome-extension://%s/success.html?access_token=%s&token_type=Bearer&expires_at=%d",
                extentionId,
                tokenDto.token(),
                tokenDto.expiresAt().getEpochSecond()
        );
        return URI.create(uriString);
    }

    public User handleOauthCallback(String code) {
        NotionWorkspaceResponseDto rawWorkpsace = this.workspaceService.exchangeCodeForWorkspaceData(code);
        User user = userService.findOrCreateUser(new CreateUserDto(
                rawWorkpsace.getOwner().getUser().getId(),
                rawWorkpsace.getOwner().getType()
        ));

        Workspace workspace = workspaceService.createOrUpdate(new CreateWorkspaceDto(
                rawWorkpsace.getWorkspaceId(),
                user,
                rawWorkpsace.getAccessToken(),
                rawWorkpsace.getWorkspaceName(),
                rawWorkpsace.getWorkspaceIcon()
        ));

        // TODO Create a job to fetch pages later
        this.pagesService.fetchPagesByWorkspace(workspace);

        return user;
    }


}
