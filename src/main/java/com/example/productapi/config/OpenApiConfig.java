package com.example.productapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product API")
                        .version("2.0.0")
                        .description("""
                                Production-ready Spring Boot 4 Product CRUD API with Hibernate Envers auditing.

                                Features:
                                - Full CRUD operations for products
                                - Automatic audit trail with revision history
                                - Paginated revision endpoint per product""")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@example.com")));
    }
}
