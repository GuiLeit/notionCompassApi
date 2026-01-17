package com.guilhermeleite.NotionCompass.controllers;

import com.guilhermeleite.NotionCompass.services.NotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok().build();
    }

}
