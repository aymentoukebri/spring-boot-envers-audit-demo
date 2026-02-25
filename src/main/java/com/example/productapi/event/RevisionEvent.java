package com.example.productapi.event;

import org.hibernate.envers.RevisionType;

import java.time.Instant;

/**
 * Domain event published every time Envers records a revision
 * for any @Audited entity.
 */
public record RevisionEvent(
        String entityType,
        Object entityId,
        RevisionType revisionType,
        int revisionNumber,
        Instant timestamp
) {
}
