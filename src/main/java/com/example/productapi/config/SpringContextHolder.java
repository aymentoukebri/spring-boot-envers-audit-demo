package com.example.productapi.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Static holder for the Spring ApplicationContext.
 * Needed because Hibernate instantiates the RevisionListener — not Spring —
 * so we cannot inject beans into it. This bridge gives the listener
 * access to the ApplicationEventPublisher.
 */
@Component
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    public static ApplicationContext getContext() {
        return context;
    }
}
