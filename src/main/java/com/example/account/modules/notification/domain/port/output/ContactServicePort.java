package com.example.account.modules.notification.domain.port.output;

import com.example.account.modules.notification.dto.AddContactRequest;
import com.example.account.modules.notification.dto.ContactResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ContactServicePort {
    Mono<ContactResponse> addContact(AddContactRequest request);
    Flux<ContactResponse> listContacts(UUID organizationId);
    Mono<Void> blockContact(UUID contactId);
    Mono<Void> removeContact(UUID contactId);
}
