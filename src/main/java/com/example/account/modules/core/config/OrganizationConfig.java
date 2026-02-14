package com.example.account.modules.core.config;

import com.example.account.modules.core.context.ReactiveOrganizationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Configuration
@EnableR2dbcAuditing(auditorAwareRef = "auditorProvider")
public class OrganizationConfig {

    @Bean
    public ReactiveAuditorAware<UUID> auditorProvider() {
        return () -> ReactiveOrganizationContext.getUserId();
    }
}
