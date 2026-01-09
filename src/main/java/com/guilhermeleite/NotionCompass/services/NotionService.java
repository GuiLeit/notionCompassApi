package com.guilhermeleite.NotionCompass.services;

import com.guilhermeleite.NotionCompass.config.NotionProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class NotionService {

    private final NotionProperties notionProperties;

    public URI buildAuthorizationUri(URI callbackUri) {
        String uriString = String.format("%s?client_id=%s&response_type=code&owner=user&redirect_uri=%s",
                notionProperties.getAuthorizationUrl(),
                notionProperties.getClientId(),
                callbackUri.toString());
        return URI.create(uriString);
    }

}
