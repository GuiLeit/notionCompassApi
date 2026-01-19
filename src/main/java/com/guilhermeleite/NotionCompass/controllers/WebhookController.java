package com.guilhermeleite.NotionCompass.controllers;

import com.guilhermeleite.NotionCompass.dtos.notion.NotionWebhookPayloadDto;
import com.guilhermeleite.NotionCompass.services.NotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final NotionService notionService;

    @PostMapping()
    public ResponseEntity<?> index(
            @RequestHeader("x-notion-signature") String notionSignature,
            @RequestBody String requestBody
    ) {
        if (!this.notionService.isWebhookSignatureValid(notionSignature, requestBody)) {
            return ResponseEntity.status(401).build();
        }
        ObjectMapper mapper = new ObjectMapper();
        NotionWebhookPayloadDto payload = mapper.readValue(requestBody, NotionWebhookPayloadDto.class);
        this.notionService.handleWebhookEvent(payload);


        return ResponseEntity.ok().build();
    }

}
