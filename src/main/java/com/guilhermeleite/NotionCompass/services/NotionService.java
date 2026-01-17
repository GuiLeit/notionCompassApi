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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotionService {

    private final UserService userService;
    private final WorkspaceService workspaceService;
    private final PagesService pagesService;

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
