package com.guilhermeleite.NotionCompass.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Health", description = "API health check")
@RestController
@RequestMapping("/")
public class HomeController {

    @Operation(summary = "Health check", description = "Returns a welcome message confirming the API is up")
    @ApiResponse(responseCode = "200", description = "API is running")
    @GetMapping()
    public ResponseEntity<Map<String, String>> home() {
        return ResponseEntity.ok(Map.of("message", "Welcome to Notion Compass! Teste"));
    }
}
