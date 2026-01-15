package com.guilhermeleite.NotionCompass.controllers;

import com.guilhermeleite.NotionCompass.domains.user.User;
import com.guilhermeleite.NotionCompass.dtos.NotionCallbackRequestDto;
import com.guilhermeleite.NotionCompass.services.NotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/notion/auth")
@RequiredArgsConstructor
public class OauthController {

    private final NotionService notionService;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/login")
    public ResponseEntity<Void> login() {
        URI authorizationUri = notionService.buildAuthorizationUri();
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                .location(authorizationUri)
                .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<?> callback(@Valid @ModelAttribute NotionCallbackRequestDto request) {
        User user = notionService.handleOauthCallback(request.getCode());
        var usernamePassword = new UsernamePasswordAuthenticationToken(user.getNotionUserId(), "");
        var auth = this.authenticationManager.authenticate(usernamePassword);

        return ResponseEntity.ok(Map.of("code", request.getCode()));
    }
}
