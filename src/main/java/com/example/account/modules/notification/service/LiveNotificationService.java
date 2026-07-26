package com.example.account.modules.notification.service;

import com.example.account.modules.facturation.domain.port.output.FactureServicePort;
import com.example.account.modules.facturation.dto.response.FactureResponse;
import com.example.account.modules.notification.domain.port.output.NotificationPort;
import com.example.account.modules.notification.model.entity.Contact;
import com.example.account.modules.notification.model.entity.LiveNotification;
import com.example.account.modules.notification.model.enums.ContactStatus;
import com.example.account.modules.notification.repository.ContactRepository;
import com.example.account.modules.notification.repository.LiveNotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LiveNotificationService {

    private final LiveNotificationRepository liveNotificationRepository;
    private final ContactRepository contactRepository;
    private final FactureServicePort factureServicePort;
    private final NotificationPort notificationPort;

    public LiveNotificationService(LiveNotificationRepository liveNotificationRepository,
                                    ContactRepository contactRepository,
                                    FactureServicePort factureServicePort,
                                    NotificationPort notificationPort) {
        this.liveNotificationRepository = liveNotificationRepository;
        this.contactRepository = contactRepository;
        this.factureServicePort = factureServicePort;
        this.notificationPort = notificationPort;
    }

    // ─── Rule management ────────────────────────────────────────────────────

    public Mono<LiveNotification> create(UUID organizationId, List<UUID> contactIds, long delayMinutes) {
        Instant now = Instant.now();
        LiveNotification liveNotification = LiveNotification.builder()
                .id(UUID.randomUUID())
                .organizationId(organizationId)
                .contactIds(contactIds == null ? new UUID[0] : contactIds.toArray(new UUID[0]))
                .pendingFactureIds(new UUID[0])
                .delayMinutes(delayMinutes)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
        return liveNotificationRepository.save(liveNotification);
    }

    public Mono<LiveNotification> addContact(UUID liveNotificationId, UUID contactId) {
        return findOrThrow(liveNotificationId).flatMap(ln -> {
            ln.setContactIds(withAdded(ln.getContactIds(), contactId));
            ln.setUpdatedAt(Instant.now());
            return liveNotificationRepository.save(ln);
        });
    }

    public Mono<LiveNotification> removeContact(UUID liveNotificationId, UUID contactId) {
        return findOrThrow(liveNotificationId).flatMap(ln -> {
            ln.setContactIds(withRemoved(ln.getContactIds(), contactId));
            ln.setUpdatedAt(Instant.now());
            return liveNotificationRepository.save(ln);
        });
    }

    public Mono<LiveNotification> updateDelay(UUID liveNotificationId, long delayMinutes) {
        return findOrThrow(liveNotificationId).flatMap(ln -> {
            ln.setDelayMinutes(delayMinutes);
            ln.setUpdatedAt(Instant.now());
            return liveNotificationRepository.save(ln);
        });
    }

    // ─── Queueing ────────────────────────────────────────────────────────────

    /**
     * Queues a facture id on every active LiveNotification rule for the organization,
     * without ever writing to the factures table itself.
     */
    public Mono<Void> enqueueFacture(UUID organizationId, UUID factureId) {
        return liveNotificationRepository.findByOrganizationId(organizationId)
                .filter(LiveNotification::isActive)
                .flatMap(ln -> {
                    ln.setPendingFactureIds(withAdded(ln.getPendingFactureIds(), factureId));
                    ln.setUpdatedAt(Instant.now());
                    return liveNotificationRepository.save(ln);
                })
                .then();
    }

    // ─── Flush ───────────────────────────────────────────────────────────────

    @Scheduled(fixedDelayString = "${live-notification.scheduler.tick-ms:60000}")
    public void flushDueScheduled() {
        flushDue().subscribe(
                null,
                ex -> log.warn("Live notification flush failed: {}", ex.getMessage())
        );
    }

    public Mono<Void> flushDue() {
        return liveNotificationRepository.findByActiveTrue()
                .filter(this::isDue)
                .flatMap(this::flushOne)
                .then();
    }

    private boolean isDue(LiveNotification ln) {
        if (ln.getPendingFactureIds() == null || ln.getPendingFactureIds().length == 0) {
            return false;
        }
        if (ln.getLastSentAt() == null) {
            return true;
        }
        Instant dueAt = ln.getLastSentAt().plus(ln.getDelayMinutes(), ChronoUnit.MINUTES);
        return !Instant.now().isBefore(dueAt);
    }

    private Mono<Void> flushOne(LiveNotification ln) {
        List<UUID> factureIds = Arrays.asList(ln.getPendingFactureIds());
        List<UUID> contactIds = Arrays.asList(ln.getContactIds());

        Mono<List<FactureResponse>> factures = Flux.fromIterable(factureIds)
                .flatMap(id -> factureServicePort.findById(id).onErrorResume(e -> Mono.empty()))
                .collectList();
        Mono<List<Contact>> activeContacts = contactRepository.findAllById(contactIds)
                .filter(c -> ContactStatus.ACTIVE.name().equals(c.getStatus()))
                .collectList();

        return factures.zipWith(activeContacts)
                .flatMap(tuple -> {
                    List<FactureResponse> factureList = tuple.getT1();
                    List<Contact> contactList = tuple.getT2();
                    if (factureList.isEmpty() || contactList.isEmpty()) {
                        return clearQueue(ln);
                    }
                    return notificationPort.send(buildMessage(factureList), contactList)
                            .then(clearQueue(ln));
                });
    }

    private Mono<Void> clearQueue(LiveNotification ln) {
        ln.setPendingFactureIds(new UUID[0]);
        ln.setLastSentAt(Instant.now());
        ln.setUpdatedAt(Instant.now());
        return liveNotificationRepository.save(ln).then();
    }

    private String buildMessage(List<FactureResponse> factures) {
        StringBuilder sb = new StringBuilder();
        sb.append("📋 ").append(factures.size())
                .append(factures.size() == 1 ? " new invoice" : " new invoices").append("\n");
        for (FactureResponse f : factures) {
            sb.append("• ")
                    .append(f.getNumeroFacture() != null ? f.getNumeroFacture() : f.getIdFacture())
                    .append(" — ")
                    .append(f.getMontantTTC() != null ? f.getMontantTTC() : f.getMontantTotal())
                    .append(f.getDevise() != null ? " " + f.getDevise() : "")
                    .append(f.getNomClient() != null ? " — " + f.getNomClient() : "")
                    .append("\n");
        }
        return sb.toString().trim();
    }

    // ─── Array helpers (contact_ids / pending_facture_ids are plain Postgres UUID[]) ──

    private UUID[] withAdded(UUID[] current, UUID id) {
        Set<UUID> set = current == null ? new LinkedHashSet<>() : new LinkedHashSet<>(Arrays.asList(current));
        set.add(id);
        return set.toArray(new UUID[0]);
    }

    private UUID[] withRemoved(UUID[] current, UUID id) {
        if (current == null) {
            return new UUID[0];
        }
        return Arrays.stream(current).filter(existing -> !existing.equals(id)).collect(Collectors.toList()).toArray(new UUID[0]);
    }

    private Mono<LiveNotification> findOrThrow(UUID id) {
        return liveNotificationRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Live notification not found: " + id)));
    }
}
