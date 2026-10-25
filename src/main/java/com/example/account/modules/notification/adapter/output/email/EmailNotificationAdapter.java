package com.example.account.modules.notification.adapter.output.email;

import com.example.account.modules.notification.domain.port.output.NotificationPort;
import com.example.account.modules.notification.model.entity.Contact;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

/**
 * EMAIL-channel counterpart to TelegramNotificationAdapter. Deliberately on
 * direct SMTP (JavaMailSender) rather than Kernel's notification-core: this
 * fires from the live-notification @Scheduled flush, which has no caller
 * token (it's a background tick, not an HTTP request) — Kernel's
 * /api/notifications/deliveries needs one, SMTP just needs the static
 * credentials already configured, so it's the only path that actually works
 * from here.
 */
@Service
@Qualifier("emailNotificationPort")
@Slf4j
public class EmailNotificationAdapter implements NotificationPort {

    private final JavaMailSender mailSender;

    public EmailNotificationAdapter(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public Mono<Void> send(String message, List<Contact> recipients) {
        return Flux.fromIterable(recipients)
                .filter(contact -> contact.getEmail() != null && !contact.getEmail().isBlank())
                .flatMap(contact -> sendOne(contact, message))
                .then();
    }

    private Mono<Void> sendOne(Contact contact, String message) {
        return Mono.fromRunnable(() -> {
                    try {
                        MimeMessage mimeMessage = mailSender.createMimeMessage();
                        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
                        helper.setTo(contact.getEmail());
                        helper.setSubject("Nouvelles factures — KSM");
                        helper.setText("<p>" + message.replace("\n", "<br/>") + "</p>", true);
                        mailSender.send(mimeMessage);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(ex -> {
                    log.warn("Live-notification email send failed for contact {}: {}", contact.getId(), ex.getMessage());
                    return Mono.empty();
                })
                .then();
    }
}
