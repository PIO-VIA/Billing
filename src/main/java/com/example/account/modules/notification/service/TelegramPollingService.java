package com.example.account.modules.notification.service;

import com.example.account.modules.notification.adapter.output.telegram.TelegramClient;
import com.example.account.modules.notification.adapter.output.telegram.dto.TelegramUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Discovers new contacts: polls getUpdates for /start <token> messages and
 * links the sender's chatId to the matching Contact. No public webhook needed.
 */
@Component
@Slf4j
public class TelegramPollingService {

    private final TelegramClient telegramClient;
    private final ContactService contactService;

    @Value("${telegram.bot.token}")
    private String botToken;

    private final AtomicLong offset = new AtomicLong(0);

    public TelegramPollingService(TelegramClient telegramClient, ContactService contactService) {
        this.telegramClient = telegramClient;
        this.contactService = contactService;
    }

    @Scheduled(fixedDelayString = "${telegram.poll.delay-ms:5000}")
    public void poll() {
        if (botToken == null || botToken.isBlank()) {
            return;
        }
        telegramClient.getUpdates(offset.get())
                .flatMapMany(Flux::fromIterable)
                .doOnNext(this::advanceOffset)
                .filter(this::isStartCommand)
                .flatMap(this::handleStart)
                .onErrorResume(ex -> {
                    log.warn("Telegram polling failed: {}", ex.getMessage());
                    return Mono.empty();
                })
                .subscribe();
    }

    private void advanceOffset(TelegramUpdate update) {
        offset.updateAndGet(current -> Math.max(current, update.getUpdateId() + 1));
    }

    private boolean isStartCommand(TelegramUpdate update) {
        return update.getMessage() != null
                && update.getMessage().getChat() != null
                && update.getMessage().getText() != null
                && update.getMessage().getText().startsWith("/start ");
    }

    private Mono<Void> handleStart(TelegramUpdate update) {
        String token = update.getMessage().getText().substring("/start ".length()).trim();
        String chatId = String.valueOf(update.getMessage().getChat().getId());
        return contactService.linkChat(token, chatId)
                .doOnNext(contact -> log.info("Linked contact {} to chat {}", contact.getId(), chatId))
                .onErrorResume(ex -> {
                    log.warn("Failed to link chat {} with token {}: {}", chatId, token, ex.getMessage());
                    return Mono.empty();
                })
                .then();
    }
}
