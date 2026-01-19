package com.guilhermeleite.NotionCompass.dtos.notion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotionWorkspaceResponseDto {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("bot_id")
    private String botId;

    @JsonProperty("workspace_name")
    private String workspaceName;

    @JsonProperty("workspace_icon")
    private String workspaceIcon;

    @JsonProperty("workspace_id")
    private String workspaceId;

    private Owner owner;

    @JsonProperty("duplicated_template_id")
    private String duplicatedTemplateId;

    @JsonProperty("request_id")
    private String requestId;

    @Getter
    @Setter
    public static class Owner {
        private String type;
        private User user;
    }

    @Getter
    @Setter
    public static class User {
        private String object;
        private String id;
    }

    // Response
    // {
    //     "access_token": "ntn_37...",
    //     "token_type": "bearer",
    //     "refresh_token": null,
    //     "bot_id": "22d...",
    //     "workspace_name": "Estudo",
    //     "workspace_icon": "📚",
    //     "workspace_id": "e55...",
    //     "owner": {
    //         "type": "user",
    //         "user": {
    //             "object": "user",
    //             "id": "97b..."
    //         }
    //     },
    //     "duplicated_template_id": null,
    //     "request_id": "81f..."
    // }
}
