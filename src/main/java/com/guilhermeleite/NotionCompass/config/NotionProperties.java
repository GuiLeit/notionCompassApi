package com.guilhermeleite.NotionCompass.config;

import com.guilhermeleite.NotionCompass.dtos.AuthTokenDetailsDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

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
    private String redirectUri;
    private String tokenUrl;
    private String pagesUrl;

    @Value("${app.extension.id}")
    private String extentionId;

    public NotionProperties() {
        this.authorizationUrl = "https://api.notion.com/v1/oauth/authorize";
        this.tokenUrl = "https://api.notion.com/v1/oauth/token";
        this.pagesUrl = "https://api.notion.com/v1/search";
    }

    public URI getCallbackUri() {
        if(this.getRedirectUri().startsWith("http")) {
            return URI.create(this.getRedirectUri());
        }
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(this.getRedirectUri())
                .build()
                .toUri();
    }

    public URI buildAuthorizationUri() {
        String uriString = String.format("%s?client_id=%s&response_type=code&owner=user&redirect_uri=%s",
                this.getAuthorizationUrl(),
                this.getClientId(),
                getCallbackUri().toString());
        return URI.create(uriString);
    }

    public URI buildSuccessAuthorizationUri(AuthTokenDetailsDto tokenDto) {
        String uriString = String.format("chrome-extension://%s/success.html?access_token=%s&token_type=Bearer&expires_at=%d",
                this.extentionId,
                tokenDto.token(),
                tokenDto.expiresAt().getEpochSecond()
        );
        return URI.create(uriString);
    }
}

