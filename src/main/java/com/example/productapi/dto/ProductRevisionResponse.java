package com.example.productapi.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ProductRevisionResponse(
        Integer revisionNumber,
        String revisionType,
        Instant revisionTimestamp,
        ProductResponse product
) {
}
