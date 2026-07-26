package com.example.account.modules.notification.application.usecase.impl;

import com.example.account.modules.core.context.ReactiveOrganizationContext;
import com.example.account.modules.notification.domain.port.input.ContactUseCase;
import com.example.account.modules.notification.domain.port.output.ContactServicePort;
import com.example.account.modules.notification.dto.AddContactRequest;
import com.example.account.modules.notification.dto.ContactResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactUseCaseImpl implements ContactUseCase {

    private final ContactServicePort contactServicePort;

    @Override
    public Mono<ContactResponse> addContact(AddContactRequest request) {
        return ReactiveOrganizationContext.getOrganizationId()
                .flatMap(orgId -> {
                    request.setOrganizationId(orgId);
                    return contactServicePort.addContact(request);
                });
    }

    @Override
    public Flux<ContactResponse> listContacts() {
        return ReactiveOrganizationContext.getOrganizationId()
                .flatMapMany(contactServicePort::listContacts);
    }

    @Override
    public Mono<Void> blockContact(UUID contactId) {
        return contactServicePort.blockContact(contactId);
    }

    @Override
    public Mono<Void> removeContact(UUID contactId) {
        return contactServicePort.removeContact(contactId);
    }
}
