package com.guilhermeleite.NotionCompass.controllers;

import com.guilhermeleite.NotionCompass.config.NotionProperties;
import com.guilhermeleite.NotionCompass.domains.user.User;
import com.guilhermeleite.NotionCompass.dtos.notion.NotionCallbackRequestDto;
import com.guilhermeleite.NotionCompass.dtos.AuthTokenDetailsDto;
import com.guilhermeleite.NotionCompass.security.TokenService;
import com.guilhermeleite.NotionCompass.services.NotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/notion/auth")
@RequiredArgsConstructor
public class OauthController {

    private final NotionProperties  notionProperties;
    private final NotionService notionService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @GetMapping("/login")
    public ResponseEntity<Void> login() {
        URI authorizationUri = this.notionProperties.buildAuthorizationUri();
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                .location(authorizationUri)
                .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<?> callback(@Valid @ModelAttribute NotionCallbackRequestDto request) {
        User user = notionService.handleOauthCallback(request.getCode());

        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(user.getNotionUserId(), "password");
        Authentication auth = this.authenticationManager.authenticate(usernamePassword);

        AuthTokenDetailsDto tokenDto = tokenService.generateToken((User) auth.getPrincipal());
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                .location(this.notionProperties.buildSuccessAuthorizationUri(tokenDto))
                .build();
    }
}
