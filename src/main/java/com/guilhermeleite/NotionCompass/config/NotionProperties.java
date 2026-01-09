package com.guilhermeleite.NotionCompass.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for Notion API
 * This approach is better for managing multiple related properties
 */
@Configuration
@ConfigurationProperties(prefix = "notion")
@Getter
@Setter
public class NotionProperties {

    private String apiKey;
    private String clientId;
    private String clientSecret;
    private String authorizationUrl;
    private String tokenUrl;

    public NotionProperties() {
        this.authorizationUrl = "https://api.notion.com/v1/oauth/authorize";
        this.tokenUrl = "https://api.notion.com/v1/oauth/token";
    }

}

