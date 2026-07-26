package com.example.account.modules.notification.service;

import com.example.account.modules.notification.model.entity.Contact;
import com.example.account.modules.notification.model.enums.ContactStatus;
import com.example.account.modules.notification.repository.ContactRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Value;
import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
public class ContactService {

    private final ContactRepository contactRepository;
    private final ContactEmailService contactEmailService;

    @Value("${telegram.bot.username:KSM_BILL_bot}")
    private String botUsername;

    public ContactService(ContactRepository contactRepository, ContactEmailService contactEmailService) {
        this.contactRepository = contactRepository;
        this.contactEmailService = contactEmailService;
    }

    public Mono<Contact> addContact(UUID organizationId, String name, String email) {
        Instant now = Instant.now();
        Contact contact = Contact.builder()
                .id(UUID.randomUUID())
                .name(name)
                .email(email)
                .organizationId(organizationId)
                .linkToken(UUID.randomUUID().toString().replace("-", ""))
                .status(ContactStatus.PENDING.name())
                .createdAt(now)
                .updatedAt(now)
                .build();

        String deepLink = "https://t.me/" + botUsername + "?start=" + contact.getLinkToken();
        com.example.account.modules.notification.dto.SendContactInvitationEmailRequest request = new com.example.account.modules.notification.dto.SendContactInvitationEmailRequest();
        request.setOrganizationId(contact.getOrganizationId());
        request.setEmail(contact.getEmail());
        request.setName(contact.getName());
        request.setTelegramDeepLink(deepLink);

        return contactRepository.save(contact)
                .flatMap(saved -> contactEmailService.sendInvitation(request).thenReturn(saved));
    }

    public Mono<Contact> linkChat(String linkToken, String chatId) {
        return contactRepository.findByLinkToken(linkToken)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown invite token")))
                .flatMap(contact -> {
                    contact.setChatId(chatId);
                    contact.setStatus(ContactStatus.ACTIVE.name());
                    contact.setUpdatedAt(Instant.now());
                    return contactRepository.save(contact);
                });
    }

    public Mono<Void> blockContact(UUID contactId) {
        return setStatus(contactId, ContactStatus.BLOCKED_BY_ADMIN);
    }

    public Mono<Void> unblockContact(UUID contactId) {
        return setStatus(contactId, ContactStatus.ACTIVE);
    }

    public Mono<Void> removeContact(UUID contactId) {
        return contactRepository.findById(contactId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found: " + contactId)))
                .flatMap(contactRepository::delete);
    }

    public Mono<Void> markBlockedByUser(String chatId) {
        return contactRepository.findByChatId(chatId)
                .flatMap(contact -> {
                    contact.setStatus(ContactStatus.BLOCKED_BY_USER.name());
                    contact.setUpdatedAt(Instant.now());
                    return contactRepository.save(contact);
                })
                .then();
    }

    public Flux<Contact> listContacts(UUID organizationId) {
        return contactRepository.findByOrganizationId(organizationId);
    }

    private Mono<Void> setStatus(UUID contactId, ContactStatus status) {
        return contactRepository.findById(contactId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found: " + contactId)))
                .flatMap(contact -> {
                    contact.setStatus(status.name());
                    contact.setUpdatedAt(Instant.now());
                    return contactRepository.save(contact);
                })
                .then();
    }
}
