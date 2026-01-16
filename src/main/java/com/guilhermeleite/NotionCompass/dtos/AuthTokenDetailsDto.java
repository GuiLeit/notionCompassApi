package com.guilhermeleite.NotionCompass.dtos;

import java.time.Instant;

public record AuthTokenDetailsDto(
        String token,
        Instant expiresAt
) {
}
