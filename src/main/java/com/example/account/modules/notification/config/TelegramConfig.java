package com.example.account.modules.notification.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class TelegramConfig {

    @Value("${telegram.api-base-url}")
    private String apiBaseUrl;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Bean
    @Qualifier("telegramWebClient")
    public WebClient telegramWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(apiBaseUrl + "/bot" + botToken)
                .build();
    }
}
