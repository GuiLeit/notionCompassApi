package com.guilhermeleite.NotionCompass.controllers;

import com.guilhermeleite.NotionCompass.dtos.NotionCallbackRequestDto;
import com.guilhermeleite.NotionCompass.services.NotionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/notion")
@RequiredArgsConstructor
public class NotionOauthController {

    private final NotionService notionService;

    @GetMapping("/login")
    public ResponseEntity<Void> login() {
        URI authorizationUri = notionService.buildAuthorizationUri();
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                .location(authorizationUri)
                .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<?> callback(@Valid @ModelAttribute NotionCallbackRequestDto request) {
        notionService.handleOauthCallback(request.getCode());
        return ResponseEntity.ok(Map.of("code", request.getCode()));
    }
}
