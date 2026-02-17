package com.example.account.modules.facturation.service;

import com.example.account.modules.facturation.dto.request.FactureCreateRequest;
import com.example.account.modules.facturation.dto.response.FactureResponse;
import com.example.account.modules.facturation.dto.response.ExternalResponses.SellerAuthResponse;
import com.example.account.modules.facturation.mapper.FactureMapper;
import com.example.account.modules.facturation.model.entity.Facture;
import com.example.account.modules.facturation.model.enums.StatutFacture;
import com.example.account.modules.facturation.repository.FactureRepository;
import com.example.account.modules.facturation.repository.DevisRepository;
import com.example.account.modules.tiers.model.entity.Client;
import com.example.account.modules.tiers.repository.ClientRepository;
import com.example.account.modules.facturation.service.ExternalServices.AccountingService;
import com.example.account.modules.facturation.service.ExternalServices.SellerService;
import com.example.account.modules.facturation.service.producer.FactureEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
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
    private final AccountingService accountingService;
    private final SellerService sellerService;

    private final ClientRepository clientRepository;
    private final R2dbcEntityTemplate entityTemplate;

    @Transactional
    public Mono<FactureResponse> createFacture(FactureCreateRequest request) {
        log.info("Création d'une nouvelle facture pour le client: {}", request.getIdClient());

        Facture facture = factureMapper.toEntity(request);
        if (facture.getIdFacture() == null) {
            facture.setIdFacture(UUID.randomUUID());
        }

        return entityTemplate.insert(facture)
                .map(savedFacture -> {
                    FactureResponse response = factureMapper.toResponse(savedFacture);
                    // Non-blocking publish
                    factureEventProducer.publishFactureCreated(response);
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
                    factureEventProducer.publishFactureUpdated(response);
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
    public Mono<Void> accountFacture(UUID factureId) {
        return accountingService.sendFactureData(factureId)
                .onErrorResume(e -> {
                    log.error("Failed to sync facture {} with accounting: {}", factureId, e.getMessage());
                    return Mono.error(new Exception("Accounting sync failed: " + e.getMessage()));
                });
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
        // In R2DBC, we often use manual pagination or Flux.skip/limit
        return factureRepository.findAll()
                .skip(pageable.getOffset())
                .take(pageable.getPageSize())
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
                    return factureRepository.deleteById(factureId)
                            .then(Mono.fromRunnable(() -> factureEventProducer.publishFactureDeleted(factureId)));
                })
                .then()
                .doOnSuccess(v -> log.info("Facture supprimée avec succès: {}", factureId));
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
                .map(updatedFacture -> {
                    FactureResponse response = factureMapper.toResponse(updatedFacture);
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

                    return factureRepository.save(facture)
                            .map(updatedFacture -> {
                                FactureResponse response = factureMapper.toResponse(updatedFacture);
                                if (nouveauMontantRestant.compareTo(BigDecimal.ZERO) == 0) {
                                    factureEventProducer.publishFacturePaid(response);
                                }
                                return response;
                            });
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

                    // Envoyer l'email de rappel (wrapped in Mono)
                    return Mono.fromRunnable(() -> emailService.sendRappelPaiementEmail(facture, facture.getEmailClient()))
                            .subscribeOn(Schedulers.boundedElastic())
                            .then();
                })
                .doOnSuccess(v -> log.info("Rappel de paiement envoyé"));
    }

    public Flux<SellerAuthResponse> enrichFactures(UUID orgId) {
        return sellerService.getSellersByOrganization(orgId);
    }

    @Transactional(readOnly = true)
    public Mono<byte[]> genererPdfFacture(UUID factureId) {
        log.info("Génération du PDF pour la facture: {}", factureId);
        return factureRepository.findById(factureId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Facture non trouvée: " + factureId)))
                .flatMap(facture -> clientRepository.findById(facture.getIdClient())
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Client non trouvé pour la facture: " + factureId)))
                        .flatMap(client -> pdfGeneratorService.generateFacturePdf(facture, client)));
    }

    @Transactional
    public Mono<Void> envoyerFactureParEmail(UUID factureId) {
        log.info("Envoi de la facture {} par email", factureId);
        return factureRepository.findById(factureId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Facture non trouvée: " + factureId)))
                .flatMap(facture -> {
                    if (facture.getEmailClient() == null || facture.getEmailClient().isEmpty()) {
                        return Mono.error(new IllegalArgumentException("Le client n'a pas d'adresse email"));
                    }
                    return clientRepository.findById(facture.getIdClient())
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("Client non trouvé pour la facture: " + factureId)))
                            .flatMap(client -> pdfGeneratorService.generateFacturePdf(facture, client)
                                    .flatMap(pdfBytes -> emailService.sendFactureCreationEmail(facture, facture.getEmailClient(), pdfBytes))
                                    .then(Mono.fromCallable(() -> {
                                        facture.setEnvoyeParEmail(true);
                                        facture.setDateEnvoiEmail(java.time.LocalDateTime.now());
                                        return facture;
                                    }))
                                    .flatMap(factureRepository::save)
                                    .then());
                });
    }

    @Transactional
    public Mono<String> genererEtSauvegarderPdfFacture(UUID factureId) {
        log.info("Génération et sauvegarde du PDF pour la facture: {}", factureId);
        return factureRepository.findById(factureId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Facture non trouvée: " + factureId)))
                .flatMap(facture -> clientRepository.findById(facture.getIdClient())
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Client non trouvé pour la facture: " + factureId)))
                        .flatMap(client -> pdfGeneratorService.generateAndSaveFacturePdf(facture, client)
                                .flatMap(pdfPath -> {
                                    facture.setPdfPath(pdfPath);
                                    return factureRepository.save(facture).thenReturn(pdfPath);
                                })));
    }
}
