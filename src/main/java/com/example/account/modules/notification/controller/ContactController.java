package com.example.account.modules.notification.controller;

import com.example.account.modules.notification.domain.port.input.ContactUseCase;
import com.example.account.modules.notification.dto.AddContactRequest;
import com.example.account.modules.notification.dto.ContactResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications/contacts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notification Contacts", description = "Telegram contacts notified about invoices, proxied to sales-core")
public class ContactController {

    private final ContactUseCase contactUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a contact and email them a Telegram invite link")
    public Mono<ContactResponse> addContact(@Valid @RequestBody AddContactRequest request) {
        return contactUseCase.addContact(request);
    }

    @GetMapping
    @Operation(summary = "List contacts for the caller's organization")
    public Flux<ContactResponse> listContacts() {
        return contactUseCase.listContacts();
    }

    @PostMapping("/{id}/block")
    @Operation(summary = "Stop notifying a contact")
    public Mono<Void> blockContact(@PathVariable UUID id) {
        return contactUseCase.blockContact(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove a contact")
    public Mono<Void> removeContact(@PathVariable UUID id) {
        return contactUseCase.removeContact(id);
    }
}
