package com.example.account.modules.facturation.service;

import com.example.account.modules.facturation.dto.request.FactureCreateRequest;
import com.example.account.modules.facturation.dto.response.FactureResponse;
import com.example.account.modules.facturation.mapper.FactureMapper;
import com.example.account.modules.facturation.model.entity.Facture;
import com.example.account.modules.facturation.model.enums.StatutFacture;
import com.example.account.modules.facturation.repository.FactureRepository;
import com.example.account.modules.facturation.service.producer.FactureEventProducer;
import com.example.account.modules.facturation.repository.DevisRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FactureService {

    private final FactureRepository factureRepository;
    private final FactureMapper factureMapper;
    private final FactureEventProducer factureEventProducer;
    private final PdfGeneratorService pdfGeneratorService;
    private final EmailService emailService;
    private final DevisRepository devisRepository;

    @Transactional
    public Mono<FactureResponse> createFacture(FactureCreateRequest request) {
        log.info("Création d'une nouvelle facture pour le client: {}", request.getIdClient());

        Facture facture = factureMapper.toEntity(request);
        return factureRepository.save(facture)
                .map(savedFacture -> {
                    FactureResponse response = factureMapper.toResponse(savedFacture);
                    // factureEventProducer.publishFactureCreated(response);
                    log.info("Facture créée avec succès: {}", savedFacture.getNumeroFacture());
                    return response;
                });
    }

    @Transactional
    public Mono<FactureResponse> updateFacture(UUID factureId, FactureCreateRequest request) {
        log.info("Mise à jour de la facture: {}", factureId);

        return factureRepository.findById(factureId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Facture non trouvée: " + factureId)))
                .flatMap(facture -> {
                    factureMapper.updateEntityFromRequest(request, facture);
                    return factureRepository.save(facture);
                })
                .map(updatedFacture -> {
                    FactureResponse response = factureMapper.toResponse(updatedFacture);
                    // factureEventProducer.publishFactureUpdated(response);
                    log.info("Facture mise à jour avec succès: {}", factureId);
                    return response;
                });
    }

    @Transactional(readOnly = true)
    public Mono<FactureResponse> getFactureById(UUID factureId) {
        log.info("Récupération de la facture: {}", factureId);

        return factureRepository.findById(factureId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Facture non trouvée: " + factureId)))
                .map(factureMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Mono<FactureResponse> getFactureByNumero(String numeroFacture) {
        log.info("Récupération de la facture par numéro: {}", numeroFacture);

        return factureRepository.findByNumeroFacture(numeroFacture)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Facture non trouvée avec numéro: " + numeroFacture)))
                .map(factureMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<FactureResponse> getAllFactures() {
        log.info("Récupération de toutes les factures");
        return factureRepository.findAll()
                .map(factureMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<FactureResponse> getAllFactures(Pageable pageable) {
        log.info("Récupération de toutes les factures avec pagination");
        return factureRepository.findAllBy(pageable)
                .map(factureMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<FactureResponse> getFacturesByClient(UUID clientId) {
        log.info("Récupération des factures du client: {}", clientId);
        return factureRepository.findByIdClient(clientId)
                .map(factureMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<FactureResponse> getFacturesByEtat(StatutFacture etat) {
        log.info("Récupération des factures par état: {}", etat);
        return factureRepository.findByEtat(etat)
                .map(factureMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<FactureResponse> getFacturesEnRetard() {
        log.info("Récupération des factures en retard");
        return factureRepository.findOverdueFactures(LocalDate.now())
                .map(factureMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<FactureResponse> getFacturesNonPayees() {
        log.info("Récupération des factures non payées");
        return factureRepository.findUnpaidFactures()
                .map(factureMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<FactureResponse> getFacturesByPeriode(LocalDate dateDebut, LocalDate dateFin) {
        log.info("Récupération des factures entre {} et {}", dateDebut, dateFin);
        return factureRepository.findByDateFacturationBetween(dateDebut, dateFin)
                .map(factureMapper::toResponse);
    }

    @Transactional
    public Mono<Void> deleteFacture(UUID factureId) {
        log.info("Suppression de la facture: {}", factureId);

        return factureRepository.existsById(factureId)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new IllegalArgumentException("Facture non trouvée: " + factureId));
                    }
                    return factureRepository.deleteById(factureId);
                })
                .doOnSuccess(v -> {
                    factureEventProducer.publishFactureDeleted(factureId);
                    log.info("Facture supprimée avec succès: {}", factureId);
                });
    }

    @Transactional
    public Mono<FactureResponse> marquerCommePaye(UUID factureId) {
        log.info("Marquage de la facture {} comme payée", factureId);

        return factureRepository.findById(factureId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Facture non trouvée: " + factureId)))
                .flatMap(facture -> {
                    facture.setEtat(StatutFacture.PAYE);
                    facture.setMontantRestant(BigDecimal.ZERO);
                    return factureRepository.save(facture);
                })
                .map(savedFacture -> {
                    FactureResponse response = factureMapper.toResponse(savedFacture);
                    factureEventProducer.publishFacturePaid(response);
                    log.info("Facture marquée comme payée: {}", factureId);
                    return response;
                });
    }

    @Transactional
    public Mono<FactureResponse> enregistrerPaiement(UUID factureId, BigDecimal montantPaye) {
        log.info("Enregistrement d'un paiement de {} pour la facture {}", montantPaye, factureId);

        return factureRepository.findById(factureId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Facture non trouvée: " + factureId)))
                .flatMap(facture -> {
                    BigDecimal nouveauMontantRestant = facture.getMontantRestant().subtract(montantPaye);

                    if (nouveauMontantRestant.compareTo(BigDecimal.ZERO) < 0) {
                        return Mono.error(new IllegalArgumentException("Le montant payé dépasse le montant restant"));
                    }

                    facture.setMontantRestant(nouveauMontantRestant);

                    if (nouveauMontantRestant.compareTo(BigDecimal.ZERO) == 0) {
                        facture.setEtat(StatutFacture.PAYE);
                    } else {
                        facture.setEtat(StatutFacture.PARTIELLEMENT_PAYE);
                    }

                    return factureRepository.save(facture);
                })
                .map(savedFacture -> {
                    FactureResponse response = factureMapper.toResponse(savedFacture);
                    if (savedFacture.getMontantRestant().compareTo(BigDecimal.ZERO) == 0) {
                        factureEventProducer.publishFacturePaid(response);
                    }
                    return response;
                });
    }

    @Transactional(readOnly = true)
    public Mono<Long> countByEtat(StatutFacture etat) {
        return factureRepository.countByEtat(etat);
    }

    @Transactional
    public Mono<Void> envoyerRappelPaiement(UUID factureId) {
        log.info("Envoi d'un rappel de paiement pour la facture: {}", factureId);

        return factureRepository.findById(factureId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Facture non trouvée: " + factureId)))
                .flatMap(facture -> {
                    if (facture.getEmailClient() == null || facture.getEmailClient().isEmpty()) {
                        return Mono.error(new IllegalArgumentException("Le client n'a pas d'adresse email"));
                    }

                    return Mono.fromRunnable(() -> emailService.sendRappelPaiementEmail(facture, facture.getEmailClient()))
                            .subscribeOn(Schedulers.boundedElastic())
                            .then();
                })
                .doOnSuccess(v -> log.info("Rappel de paiement envoyé avec succès pour la facture: {}", factureId));
    }
}
