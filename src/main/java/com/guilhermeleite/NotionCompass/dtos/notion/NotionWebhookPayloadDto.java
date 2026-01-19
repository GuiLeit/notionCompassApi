package com.guilhermeleite.NotionCompass.dtos.notion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class NotionWebhookPayloadDto {
    @JsonProperty("id")
    private String id;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("workspace_id")
    private String workspaceId;

    @JsonProperty("workspace_name")
    private String workspaceName;

    @JsonProperty("subscription_id")
    private String subscriptionId;

    @JsonProperty("integration_id")
    private String integrationId;

    @JsonProperty("authors")
    private List<SimpleEntity> authors;

    @JsonProperty("accessible_by")
    private List<SimpleEntity> accessibleBy;

    @JsonProperty("attempt_number")
    private Integer attemptNumber;

    @JsonProperty("entity")
    private SimpleEntity entity;

    @JsonProperty("type")
    private String type;

    @JsonProperty("data")
    private Data data;

    @Getter
    @Setter
    public static class SimpleEntity{
        @JsonProperty("id")
        private String id;

        @JsonProperty("type")
        private String type;
    }

    @Getter
    @Setter
    public static class Data {
        @JsonProperty("parent")
        private ParentData parent;
    }

    @Getter
    @Setter
    public static class ParentData {
        @JsonProperty("id")
        private String id;

        @JsonProperty("type")
        private String type;

        @JsonProperty("data_source_id")
        private String dataSourceId;
    }
}
