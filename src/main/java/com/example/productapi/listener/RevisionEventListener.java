package com.example.productapi.listener;

import com.example.productapi.event.RevisionEvent;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.RevisionType;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Listens to RevisionEvents published by CustomRevisionListener
 * and logs an alert for every entity change tracked by Envers.
 *
 * This is the extension point: add Slack, email, webhook, or any
 * external notification by injecting the appropriate client here.
 */
@Slf4j
@Component
public class RevisionEventListener {

    @EventListener
    public void onRevision(RevisionEvent event) {
        String action = formatAction(event.revisionType());

        log.info("[AUDIT NOTIFICATION] Entity '{}' with id={} was {} | revision={} | timestamp={}",
                event.entityType(),
                event.entityId(),
                action,
                event.revisionNumber(),
                event.timestamp());
    }

    private String formatAction(RevisionType type) {
        return switch (type) {
            case ADD -> "CREATED";
            case MOD -> "MODIFIED";
            case DEL -> "DELETED";
        };
    }
}
