package com.example.account.modules.notification.adapter.output.telegram;

import com.example.account.modules.notification.adapter.output.telegram.dto.TelegramApiResponse;
import com.example.account.modules.notification.adapter.output.telegram.dto.TelegramUpdate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class TelegramClient {

    private final WebClient telegramWebClient;

    public TelegramClient(@Qualifier("telegramWebClient") WebClient telegramWebClient) {
        this.telegramWebClient = telegramWebClient;
    }

    public Mono<Void> sendMessage(String chatId, String text) {
        return telegramWebClient
                .post()
                .uri(u -> u.path("/sendMessage")
                        .queryParam("chat_id", chatId)
                        .queryParam("text", text)
                        .build())
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<List<TelegramUpdate>> getUpdates(long offset) {
        return telegramWebClient
                .get()
                .uri(u -> u.path("/getUpdates")
                        .queryParam("offset", offset)
                        .queryParam("timeout", 0)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<TelegramApiResponse<List<TelegramUpdate>>>() {})
                .map(TelegramApiResponse::getResult);
    }
}
