package com.example.account.modules.facturation.service;

import com.example.account.modules.core.context.OrganizationContext;
import com.example.account.modules.facturation.dto.request.PaiementCreateRequest;
import com.example.account.modules.facturation.dto.request.PaiementUpdateRequest;
import com.example.account.modules.facturation.dto.response.PaiementResponse;
import com.example.account.modules.facturation.mapper.PaiementMapper;
import com.example.account.modules.facturation.model.entity.Paiement;
import com.example.account.modules.facturation.model.enums.TypePaiement;
import com.example.account.modules.facturation.repository.PaiementRepository;
import com.example.account.modules.facturation.service.producer.PaiementEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final PaiementMapper paiementMapper;
    private final PaiementEventProducer paiementEventProducer;
    private final FactureService factureService;

    @Transactional
    public Mono<PaiementResponse> createPaiement(PaiementCreateRequest request) {
        log.info("Création d'un nouveau paiement pour le client: {}", request.getIdClient());

        return OrganizationContext.getOrganizationId()
                .flatMap(orgId -> {
                    Paiement paiement = paiementMapper.toEntity(request);
                    paiement.setOrganizationId(orgId);
                    paiement.setCreatedAt(LocalDateTime.now());
                    paiement.setUpdatedAt(LocalDateTime.now());

                    return paiementRepository.save(paiement)
                            .flatMap(savedPaiement -> {
                                Mono<Void> updateFacture = Mono.empty();
                                if (request.getIdFacture() != null) {
                                    updateFacture = factureService.enregistrerPaiement(request.getIdFacture(), request.getMontant())
                                            .then()
                                            .onErrorResume(e -> {
                                                log.error("Erreur lors de la mise à jour de la facture: {}", e.getMessage());
                                                return Mono.empty();
                                            });
                                }

                                return updateFacture.thenReturn(savedPaiement);
                            });
                })
                .map(savedPaiement -> {
                    PaiementResponse response = paiementMapper.toResponse(savedPaiement);
                    paiementEventProducer.publishPaiementCreated(response);
                    log.info("Paiement créé avec succès: {}", savedPaiement.getIdPaiement());
                    return response;
                });
    }

    @Transactional
    public Mono<PaiementResponse> updatePaiement(UUID paiementId, PaiementUpdateRequest request) {
        log.info("Mise à jour du paiement: {}", paiementId);

        return paiementRepository.findById(paiementId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Paiement non trouvé: " + paiementId)))
                .flatMap(paiement -> {
                    paiementMapper.updateEntityFromRequest(request, paiement);
                    paiement.setUpdatedAt(LocalDateTime.now());
                    return paiementRepository.save(paiement);
                })
                .map(updatedPaiement -> {
                    PaiementResponse response = paiementMapper.toResponse(updatedPaiement);
                    paiementEventProducer.publishPaiementUpdated(response);
                    log.info("Paiement mis à jour avec succès: {}", paiementId);
                    return response;
                });
    }

    @Transactional(readOnly = true)
    public Mono<PaiementResponse> getPaiementById(UUID paiementId) {
        log.info("Récupération du paiement: {}", paiementId);
        return paiementRepository.findById(paiementId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Paiement non trouvé: " + paiementId)))
                .map(paiementMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<PaiementResponse> getAllPaiements() {
        log.info("Récupération de tous les paiements");
        return paiementRepository.findAll()
                .map(paiementMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<PaiementResponse> getPaiementsByClient(UUID clientId) {
        log.info("Récupération des paiements du client: {}", clientId);
        return paiementRepository.findByIdClient(clientId)
                .map(paiementMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<PaiementResponse> getPaiementsByFacture(UUID factureId) {
        log.info("Récupération des paiements de la facture: {}", factureId);
        return paiementRepository.findByIdFacture(factureId)
                .map(paiementMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<PaiementResponse> getPaiementsByModePaiement(TypePaiement modePaiement) {
        log.info("Récupération des paiements par mode de paiement: {}", modePaiement);
        return paiementRepository.findByModePaiement(modePaiement)
                .map(paiementMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<PaiementResponse> getPaiementsByPeriode(LocalDate dateDebut, LocalDate dateFin) {
        log.info("Récupération des paiements entre {} et {}", dateDebut, dateFin);
        return paiementRepository.findByDateBetween(dateDebut, dateFin)
                .map(paiementMapper::toResponse);
    }

    @Transactional
    public Mono<Void> deletePaiement(UUID paiementId) {
        log.info("Suppression du paiement: {}", paiementId);
        return paiementRepository.existsById(paiementId)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new IllegalArgumentException("Paiement non trouvé: " + paiementId));
                    }
                    return paiementRepository.deleteById(paiementId)
                            .doOnSuccess(v -> {
                                paiementEventProducer.publishPaiementDeleted(paiementId);
                                log.info("Paiement supprimé avec succès: {}", paiementId);
                            });
                });
    }

    @Transactional(readOnly = true)
    public Mono<BigDecimal> getTotalPaiementsByClient(UUID clientId) {
        log.info("Calcul du total des paiements du client: {}", clientId);
        return paiementRepository.sumMontantByClient(clientId)
                .defaultIfEmpty(BigDecimal.ZERO);
    }

    @Transactional(readOnly = true)
    public Mono<BigDecimal> getTotalPaiementsByFacture(UUID factureId) {
        log.info("Calcul du total des paiements de la facture: {}", factureId);
        return paiementRepository.sumMontantByFacture(factureId)
                .defaultIfEmpty(BigDecimal.ZERO);
    }

    @Transactional(readOnly = true)
    public Mono<BigDecimal> getTotalPaiementsByPeriode(LocalDate dateDebut, LocalDate dateFin) {
        log.info("Calcul du total des paiements entre {} et {}", dateDebut, dateFin);
        return paiementRepository.sumMontantByDateBetween(dateDebut, dateFin)
                .defaultIfEmpty(BigDecimal.ZERO);
    }

    @Transactional(readOnly = true)
    public Mono<Long> countPaiementsByClient(UUID clientId) {
        return paiementRepository.countByIdClient(clientId);
    }

    @Transactional(readOnly = true)
    public Mono<Long> countPaiementsByModePaiement(TypePaiement modePaiement) {
        return paiementRepository.countByModePaiement(modePaiement);
    }
}
