package com.example.account.modules.facturation.service;

import com.example.account.modules.core.context.OrganizationContext;
import com.example.account.modules.facturation.dto.request.JournalCreateRequest;
import com.example.account.modules.facturation.dto.request.JournalUpdateRequest;
import com.example.account.modules.facturation.dto.response.JournalResponse;
import com.example.account.modules.facturation.mapper.JournalMapper;
import com.example.account.modules.facturation.model.entity.Journal;
import com.example.account.modules.facturation.repository.JournalRepository;
import com.example.account.modules.facturation.service.producer.JournalEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JournalService {

    private final JournalRepository journalRepository;
    private final JournalEventProducer journalEventProducer;
    private final JournalMapper journalMapper;

    @Transactional
    public Mono<JournalResponse> createJournal(JournalCreateRequest request) {
        log.info("Création d'un nouveau journal: {}", request.getNomJournal());

        return OrganizationContext.getOrganizationId()
                .flatMap(orgId -> journalRepository.existsByNomJournal(request.getNomJournal())
                        .flatMap(exists -> {
                            if (exists) {
                                return Mono.error(new IllegalArgumentException("Un journal avec ce nom existe déjà"));
                            }
                            Journal journal = journalMapper.toEntity(request);
                            journal.setOrganizationId(orgId);
                            journal.setCreatedAt(LocalDateTime.now());
                            journal.setUpdatedAt(LocalDateTime.now());
                            return journalRepository.save(journal);
                        }))
                .map(journalMapper::toResponse)
                .doOnSuccess(response -> {
                    journalEventProducer.publishJournalCreated(response);
                    log.info("Journal créé avec succès: {}", response.getIdJournal());
                });
    }

    @Transactional
    public Mono<JournalResponse> updateJournal(UUID journalId, JournalUpdateRequest request) {
        log.info("Mise à jour du journal: {}", journalId);

        return journalRepository.findById(journalId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Journal non trouvé: " + journalId)))
                .flatMap(journal -> {
                    journalMapper.updateEntityFromRequest(request, journal);
                    journal.setUpdatedAt(LocalDateTime.now());
                    return journalRepository.save(journal);
                })
                .map(journalMapper::toResponse)
                .doOnSuccess(response -> {
                    journalEventProducer.publishJournalUpdated(response);
                    log.info("Journal mis à jour avec succès: {}", journalId);
                });
    }

    @Transactional(readOnly = true)
    public Mono<JournalResponse> getJournalById(UUID journalId) {
        log.info("Récupération du journal: {}", journalId);
        return journalRepository.findById(journalId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Journal non trouvé: " + journalId)))
                .map(journalMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Mono<JournalResponse> getJournalByNom(String nomJournal) {
        log.info("Récupération du journal par nom: {}", nomJournal);
        return journalRepository.findByNomJournal(nomJournal)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Journal non trouvé avec nom: " + nomJournal)))
                .map(journalMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<JournalResponse> getAllJournals() {
        log.info("Récupération de tous les journals");
        return journalRepository.findAll()
                .map(journalMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<JournalResponse> getJournalsByType(String type) {
        log.info("Récupération des journals par type: {}", type);
        return journalRepository.findByType(type)
                .map(journalMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<JournalResponse> searchJournalsByNom(String nomJournal) {
        log.info("Recherche des journals par nom: {}", nomJournal);
        return journalRepository.findByNomJournalContaining(nomJournal)
                .map(journalMapper::toResponse);
    }

    @Transactional
    public Mono<Void> deleteJournal(UUID journalId) {
        log.info("Suppression du journal: {}", journalId);
        return journalRepository.existsById(journalId)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new IllegalArgumentException("Journal non trouvé: " + journalId));
                    }
                    return journalRepository.deleteById(journalId)
                            .doOnSuccess(v -> {
                                journalEventProducer.publishJournalDeleted(journalId);
                                log.info("Journal supprimé avec succès: {}", journalId);
                            });
                });
    }

    @Transactional(readOnly = true)
    public Mono<Long> countByType(String type) {
        return journalRepository.countByType(type);
    }
}
