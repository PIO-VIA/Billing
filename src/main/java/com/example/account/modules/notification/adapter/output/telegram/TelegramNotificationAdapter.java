package com.example.account.modules.notification.adapter.output.telegram;

import com.example.account.modules.notification.domain.port.output.NotificationPort;
import com.example.account.modules.notification.model.entity.Contact;
import com.example.account.modules.notification.service.ContactService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class TelegramNotificationAdapter implements NotificationPort {

    private final TelegramClient telegramClient;
    private final ContactService contactService;

    public TelegramNotificationAdapter(TelegramClient telegramClient, ContactService contactService) {
        this.telegramClient = telegramClient;
        this.contactService = contactService;
    }

    @Override
    public Mono<Void> send(String message, List<Contact> recipients) {
        return Flux.fromIterable(recipients)
                .filter(contact -> contact.getChatId() != null && !contact.getChatId().isBlank())
                .flatMap(contact -> sendOne(contact, message))
                .then();
    }

    private Mono<Void> sendOne(Contact contact, String message) {
        return telegramClient.sendMessage(contact.getChatId(), message)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode() == HttpStatus.FORBIDDEN) {
                        log.info("Contact {} blocked the bot, marking BLOCKED_BY_USER", contact.getId());
                        return contactService.markBlockedByUser(contact.getChatId());
                    }
                    log.warn("Telegram send failed for contact {}: {}", contact.getId(), ex.getMessage());
                    return Mono.empty();
                });
    }
}
