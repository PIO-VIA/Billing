package com.example.account.modules.facturation.service;

import com.example.account.modules.core.context.OrganizationContext;
import com.example.account.modules.facturation.dto.request.NoteCreditRequest;
import com.example.account.modules.facturation.dto.response.NoteCreditResponse;
import com.example.account.modules.facturation.mapper.NoteCreditMapper;
import com.example.account.modules.facturation.model.entity.LigneNoteCredit;
import com.example.account.modules.facturation.model.entity.NoteCredit;
import com.example.account.modules.facturation.repository.NoteCreditRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteCreditService {

    private final NoteCreditRepository noteCreditRepository;
    private final NoteCreditMapper noteCreditMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public Mono<NoteCreditResponse> createNoteCredit(NoteCreditRequest request) {
        log.info("Création d'une nouvelle note de crédit");
        
        return OrganizationContext.getOrganizationId()
                .flatMap(orgId -> {
                    NoteCredit entity = noteCreditMapper.toEntity(request);
                    entity.setOrganizationId(orgId);
                    entity.setCreatedAt(LocalDateTime.now());
                    entity.setUpdatedAt(LocalDateTime.now());
                    
                    if (entity.getLignesFacture() != null && !entity.getLignesFacture().isEmpty()) {
                        calculateMontants(entity);
                        serializeLines(entity);
                    }
                    
                    return noteCreditRepository.save(entity)
                            .map(saved -> {
                                deserializeLines(saved);
                                return saved;
                            });
                })
                .map(noteCreditMapper::toResponse);
    }

    @Transactional
    public Mono<NoteCreditResponse> updateNoteCredit(UUID id, NoteCreditRequest request) {
        log.info("Mise à jour de la note de crédit: {}", id);
        
        return OrganizationContext.getOrganizationId()
                .flatMap(orgId -> noteCreditRepository.findByIdNoteCreditAndOrganizationId(id, orgId)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Note de crédit non trouvée")))
                        .flatMap(entity -> {
                            deserializeLines(entity); // Need lines to merge/update
                            noteCreditMapper.updateEntityFromRequest(request, entity);
                            
                            if (entity.getLignesFacture() != null && !entity.getLignesFacture().isEmpty()) {
                                calculateMontants(entity);
                                serializeLines(entity);
                            }
                            
                            entity.setUpdatedAt(LocalDateTime.now());
                            return noteCreditRepository.save(entity);
                        })
                        .map(saved -> {
                            deserializeLines(saved);
                            return saved;
                        }))
                .map(noteCreditMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Mono<NoteCreditResponse> getNoteCreditById(UUID id) {
        return OrganizationContext.getOrganizationId()
                .flatMap(orgId -> noteCreditRepository.findByIdNoteCreditAndOrganizationId(id, orgId))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Note de crédit non trouvée")))
                .map(entity -> {
                    deserializeLines(entity);
                    return entity;
                })
                .map(noteCreditMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<NoteCreditResponse> getAllNoteCredits() {
        return OrganizationContext.getOrganizationId()
                .flatMapMany(noteCreditRepository::findByOrganizationId)
                .map(entity -> {
                    deserializeLines(entity);
                    return entity;
                })
                .map(noteCreditMapper::toResponse);
    }

    @Transactional
    public Mono<Void> deleteNoteCredit(UUID id) {
        return OrganizationContext.getOrganizationId()
                .flatMap(orgId -> noteCreditRepository.findByIdNoteCreditAndOrganizationId(id, orgId))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Note de crédit non trouvée")))
                .flatMap(noteCreditRepository::delete);
    }

    private void calculateMontants(NoteCredit entity) {
        if (entity.getLignesFacture() == null || entity.getLignesFacture().isEmpty()) {
            return;
        }

        BigDecimal montantHT = entity.getLignesFacture().stream()
                .filter(ligne -> !Boolean.TRUE.equals(ligne.getIsTaxLine()))
                .map(ligne -> ligne.getMontantTotal() != null ? ligne.getMontantTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal montantTVA = entity.getLignesFacture().stream()
                .filter(ligne -> Boolean.TRUE.equals(ligne.getIsTaxLine()))
                .map(ligne -> ligne.getMontantTotal() != null ? ligne.getMontantTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal montantTTC = montantHT.add(montantTVA);

        entity.setMontantHT(montantHT);
        entity.setMontantTVA(montantTVA);
        entity.setMontantTTC(montantTTC);
        entity.setMontantTotal(montantTTC);
        entity.setMontantRestant(montantTTC);
        entity.setFinalAmount(montantTTC);
    }

    private void serializeLines(NoteCredit entity) {
        try {
            if (entity.getLignesFacture() != null) {
                entity.setLignesFactureJson(objectMapper.writeValueAsString(entity.getLignesFacture()));
            }
        } catch (JsonProcessingException e) {
            log.error("Erreur lors de la sérialisation des lignes de la note de crédit", e);
            throw new RuntimeException("Erreur de sérialisation JSON", e);
        }
    }

    private void deserializeLines(NoteCredit entity) {
        try {
            if (entity.getLignesFactureJson() != null) {
                List<LigneNoteCredit> lines = objectMapper.readValue(
                        entity.getLignesFactureJson(), 
                        new TypeReference<List<LigneNoteCredit>>() {});
                entity.setLignesFacture(lines);
            } else {
                entity.setLignesFacture(Collections.emptyList());
            }
        } catch (JsonProcessingException e) {
            log.error("Erreur lors de la désérialisation des lignes de la note de crédit", e);
            entity.setLignesFacture(Collections.emptyList());
        }
    }
}
