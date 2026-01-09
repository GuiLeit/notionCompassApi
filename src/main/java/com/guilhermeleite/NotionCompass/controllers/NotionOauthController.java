package com.guilhermeleite.NotionCompass.controllers;

import com.guilhermeleite.NotionCompass.config.NotionProperties;
import com.guilhermeleite.NotionCompass.services.NotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/auth/notion")
@RequiredArgsConstructor
public class NotionOauthController {

    private final NotionService notionService;

    @GetMapping("/login")
    public ResponseEntity<Void> login(UriComponentsBuilder uriComponentsBuilder) {
        URI callbackUri = uriComponentsBuilder
                .path("/api/auth/notion/callback")
                .build()
                .toUri();
        URI authorizationUri = notionService.buildAuthorizationUri(callbackUri);
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                .location(authorizationUri)
                .build();
    }
}
