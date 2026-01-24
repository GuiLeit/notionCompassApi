package com.guilhermeleite.NotionCompass.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guilhermeleite.NotionCompass.config.NotionProperties;
import com.guilhermeleite.NotionCompass.config.exceptions.EntityNotFoundException;
import com.guilhermeleite.NotionCompass.dtos.notion.NotionWebhookPayloadDto;
import com.guilhermeleite.NotionCompass.services.NotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final NotionService notionService;
    private final NotionProperties notionProperties;
    private final ObjectMapper objectMapper;

    @Value("${app.internal.validation.token}")
    private String internalValidationToken;

    @PostMapping
    public ResponseEntity<?> index(
            @RequestHeader("x-notion-signature") String notionSignature,
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

    @GetMapping("/secret")
    public ResponseEntity<Map<String, String>> getWebhookSecret(
            @RequestHeader("authorization") String authorization
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
