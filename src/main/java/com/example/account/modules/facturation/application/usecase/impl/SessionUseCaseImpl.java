package com.example.account.modules.facturation.application.usecase.impl;

import com.example.account.modules.facturation.domain.port.input.SessionUseCase;
import com.example.account.modules.facturation.domain.port.output.SessionServicePort;
import com.example.account.modules.facturation.domain.port.output.SellerServicePort;
import com.example.account.modules.facturation.dto.request.CloseSessionRequest;
import com.example.account.modules.facturation.dto.request.CreateSessionRequest;
import com.example.account.modules.facturation.dto.request.UpdateSessionRequest;
import com.example.account.modules.facturation.dto.response.ExternalResponses.SellerListItemResponse;
import com.example.account.modules.facturation.dto.response.SessionResponse;
import com.example.account.modules.notification.dto.SendSessionCreatedEmailRequest;
import com.example.account.modules.notification.service.SessionEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionUseCaseImpl implements SessionUseCase {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final SessionServicePort sessionServicePort;
    private final SellerServicePort sellerServicePort;
    private final SessionEmailService sessionEmailService;

    @Override
    public Mono<SessionResponse> open(CreateSessionRequest request) {
        return sessionServicePort.open(request).flatMap(this::notifySessionCreated);
    }

    @Override
    public Mono<SessionResponse> schedule(CreateSessionRequest request) {
        return sessionServicePort.schedule(request).flatMap(this::notifySessionCreated);
    }

    /** Best-effort: a failed notification email must never fail session creation. */
    private Mono<SessionResponse> notifySessionCreated(SessionResponse session) {
        return sellerServicePort.listSellers(session.getOrganizationId())
                .filter(seller -> session.getSellerId().equals(seller.getId()))
                .next()
                .flatMap(seller -> sessionEmailService.sendSessionCreated(toEmailRequest(seller, session)))
                .onErrorResume(e -> {
                    log.error("Failed to send session-created email for session {}: {}", session.getId(), e.getMessage());
                    return Mono.empty();
                })
                .thenReturn(session);
    }

    private SendSessionCreatedEmailRequest toEmailRequest(SellerListItemResponse seller, SessionResponse session) {
        SendSessionCreatedEmailRequest request = new SendSessionCreatedEmailRequest();
        request.setOrganizationId(seller.getOrganizationId());
        request.setEmail(seller.getEmail());
        request.setUsername(seller.getUsername());
        request.setAgency(seller.getAgency());
        request.setSessionType(session.getType() != null ? session.getType().name() : null);
        request.setStatus(session.getStatus() != null ? session.getStatus().name() : null);
        request.setStartTime(session.getStartTime() != null ? session.getStartTime().format(DATE_FORMAT) : null);
        request.setOpeningAmount(session.getOpeningAmount());
        return request;
    }

    @Override
    public Mono<SessionResponse> start(UUID id) {
        return sessionServicePort.start(id);
    }

    @Override
    public Mono<SessionResponse> close(UUID id, CloseSessionRequest request) {
        return sessionServicePort.close(id, request);
    }

    @Override
    public Mono<SessionResponse> suspend(UUID id) {
        return sessionServicePort.suspend(id);
    }

    @Override
    public Mono<SessionResponse> resume(UUID id) {
        return sessionServicePort.resume(id);
    }

    @Override
    public Mono<SessionResponse> cancel(UUID id) {
        return sessionServicePort.cancel(id);
    }

    @Override
    public Mono<SessionResponse> reopen(UUID id) {
        return sessionServicePort.reopen(id);
    }

    @Override
    public Mono<SessionResponse> findById(UUID id) {
        return sessionServicePort.findById(id);
    }

    @Override
    public Flux<SessionResponse> findAll(UUID salesPointId, UUID sellerId, UUID organizationId, UUID agencyId) {
        return sessionServicePort.findAll(salesPointId, sellerId, organizationId, agencyId);
    }

    @Override
    public Mono<SessionResponse> update(UUID id, UpdateSessionRequest request) {
        return sessionServicePort.update(id, request);
    }

    @Override
    public Mono<Void> delete(UUID id) {
        return sessionServicePort.delete(id);
    }
}
