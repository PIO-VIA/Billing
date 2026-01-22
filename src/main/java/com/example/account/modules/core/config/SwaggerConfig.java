package com.example.account.modules.core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Facturation & Multi-Tenancy")
                        .version("1.0")
                        .description("Documentation des APIs pour le module de facturation et la gestion des organisations.")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
