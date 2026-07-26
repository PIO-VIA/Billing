package com.example.account.modules.notification.domain.port.output;

import com.example.account.modules.notification.model.entity.Contact;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Generic broadcast port — the domain talks to this, not to Telegram directly,
 * so another channel (SMS, WhatsApp, ...) can be swapped in later.
 */
public interface NotificationPort {
    Mono<Void> send(String message, List<Contact> recipients);
}
