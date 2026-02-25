package com.example.productapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.Instant;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware", dateTimeProviderRef = "utcDateTimeProvider")
public class AuditingConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        // Replace with SecurityContextHolder lookup when Spring Security is added
        return () -> Optional.of("system");
    }

    @Bean
    public DateTimeProvider utcDateTimeProvider() {
        return () -> Optional.of(Instant.now());
    }
}
