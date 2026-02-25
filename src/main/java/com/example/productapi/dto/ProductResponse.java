package com.example.productapi.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Instant createdAt,
        Instant updatedAt,
        String createdBy,
        String updatedBy
) {
}
