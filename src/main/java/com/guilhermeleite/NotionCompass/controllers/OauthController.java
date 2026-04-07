package com.guilhermeleite.NotionCompass.controllers;

import com.guilhermeleite.NotionCompass.config.NotionProperties;
import com.guilhermeleite.NotionCompass.domains.user.User;
import com.guilhermeleite.NotionCompass.dtos.notion.NotionCallbackRequestDto;
import com.guilhermeleite.NotionCompass.dtos.AuthTokenDetailsDto;
import com.guilhermeleite.NotionCompass.security.TokenService;
import com.guilhermeleite.NotionCompass.services.NotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Notion OAuth", description = "Notion OAuth 2.0 authorization flow")
@Slf4j
@RestController
@RequestMapping("/notion/auth")
@RequiredArgsConstructor
public class OauthController {

    private final NotionProperties  notionProperties;
    private final NotionService notionService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Operation(summary = "Initiate Notion OAuth login", description = "Redirects the user to Notion's authorization page")
    @ApiResponse(responseCode = "307", description = "Redirect to Notion authorization URL")
    @SecurityRequirements
    @GetMapping("/login")
    public ResponseEntity<Void> login() {
        URI authorizationUri = this.notionProperties.buildAuthorizationUri();
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                .location(authorizationUri)
                .build();
    }

    @Operation(summary = "Notion OAuth callback", description = "Handles the callback from Notion after user authorization. Exchanges the code for an access token, creates/updates the user, and redirects to the extension with a JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "307", description = "Redirect to extension success URL with JWT token"),
            @ApiResponse(responseCode = "400", description = "Missing or invalid callback parameters")
    })
    @SecurityRequirements
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
