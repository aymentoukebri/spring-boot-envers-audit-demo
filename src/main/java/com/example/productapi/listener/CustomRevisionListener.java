package com.example.productapi.listener;

import com.example.productapi.config.SpringContextHolder;
import com.example.productapi.entity.CustomRevisionEntity;
import com.example.productapi.event.RevisionEvent;
import org.hibernate.envers.EntityTrackingRevisionListener;
import org.hibernate.envers.RevisionType;
import org.springframework.context.ApplicationContext;

import java.time.Instant;

public class CustomRevisionListener implements EntityTrackingRevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
    }

    @Override
    public void entityChanged(Class entityClass,
                               String entityName,
                               Object entityId,
                               RevisionType revisionType,
                               Object revisionEntity) {
        ApplicationContext context = SpringContextHolder.getContext();
        if (context == null) {
            return;
        }

        CustomRevisionEntity rev = (CustomRevisionEntity) revisionEntity;

        context.publishEvent(new RevisionEvent(
                entityClass.getSimpleName(),
                entityId,
                revisionType,
                rev.getId(),
                Instant.ofEpochMilli(rev.getTimestamp())
        ));
    }
}
