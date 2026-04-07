package com.guilhermeleite.NotionCompass.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guilhermeleite.NotionCompass.config.NotionProperties;
import com.guilhermeleite.NotionCompass.config.exceptions.EntityNotFoundException;
import com.guilhermeleite.NotionCompass.dtos.notion.NotionWebhookPayloadDto;
import com.guilhermeleite.NotionCompass.services.NotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Webhook", description = "Notion webhook event receiver")
@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final NotionService notionService;
    private final NotionProperties notionProperties;
    private final ObjectMapper objectMapper;

    @Value("${app.internal.validation.token}")
    private String internalValidationToken;

    @Operation(summary = "Receive Notion webhook event", description = "Handles incoming events from Notion. Also responds to the initial verification challenge by storing the verification token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event processed or verification acknowledged"),
            @ApiResponse(responseCode = "401", description = "Invalid webhook signature")
    })
    @SecurityRequirements
    @PostMapping
    public ResponseEntity<?> index(
            @Parameter(description = "HMAC-SHA256 signature from Notion") @RequestHeader("x-notion-signature") String notionSignature,
            @RequestBody String requestBody
    ) throws Exception {
        NotionWebhookPayloadDto payload = objectMapper.readValue(requestBody, NotionWebhookPayloadDto.class);

        if (payload.getVerificationToken() != null) {
            notionProperties.setWebhookSecret(payload.getVerificationToken());
            return ResponseEntity.ok().build();
        }

        if (!this.notionService.isWebhookSignatureValid(notionSignature, requestBody)) {
            return ResponseEntity.status(401).build();
        }
        this.notionService.handleWebhookEvent(payload);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get webhook verification token", description = "Internal endpoint that returns the stored Notion webhook verification token. Requires the internal validation token as a Bearer token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token returned"),
            @ApiResponse(responseCode = "401", description = "Invalid or missing authorization"),
            @ApiResponse(responseCode = "404", description = "Webhook secret not set yet")
    })
    @SecurityRequirements
    @GetMapping("/secret")
    public ResponseEntity<Map<String, String>> getWebhookSecret(
            @Parameter(description = "Internal Bearer token (app.internal.validation.token)") @RequestHeader("authorization") String authorization
    ) {
        if(this.internalValidationToken == null || !authorization.equals("Bearer " + this.internalValidationToken)) {
            return ResponseEntity.status(401).build();
        }
        if(this.notionProperties.getWebhookSecret() == null) {
            throw new EntityNotFoundException("Webhook secret not set", null);
        }
        return ResponseEntity.ok(Map.of(
                "verificationToken", this.notionProperties.getWebhookSecret()
        ));
    }
}
