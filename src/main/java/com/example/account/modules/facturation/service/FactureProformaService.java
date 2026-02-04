package com.example.account.modules.facturation.service;

import com.example.account.modules.core.context.OrganizationContext;
import com.example.account.modules.facturation.dto.request.ProformaInvoiceRequest;
import com.example.account.modules.facturation.dto.response.ProformaInvoiceResponse;
import com.example.account.modules.facturation.mapper.FactureProformaMapper;
import com.example.account.modules.facturation.model.entity.FactureProforma;
import com.example.account.modules.facturation.model.entity.LigneFactureProforma;
import com.example.account.modules.facturation.model.enums.StatutProforma;
import com.example.account.modules.facturation.repository.FactureProformaRepository;
import com.example.account.modules.facturation.repository.LigneFactureProformaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FactureProformaService {

    private final FactureProformaRepository proformaRepository;
    private final LigneFactureProformaRepository ligneProformaRepository;
    private final FactureProformaMapper proformaMapper;

    @Transactional
    public Mono<ProformaInvoiceResponse> createProforma(ProformaInvoiceRequest request) {
        log.info("Création d'une nouvelle facture proforma pour le client: {}", request.getIdClient());

        return OrganizationContext.getOrganizationId()
                .flatMap(orgId -> {
                    FactureProforma proforma = proformaMapper.toEntity(request);
                    
                    if (proforma.getNumeroProformaInvoice() == null || proforma.getNumeroProformaInvoice().isBlank()) {
                        proforma.setNumeroProformaInvoice("PRO-" + System.currentTimeMillis());
                    }

                    proforma.setDateCreation(LocalDateTime.now());
                    proforma.setOrganizationId(orgId);
                    proforma.setCreatedAt(LocalDateTime.now());
                    proforma.setUpdatedAt(LocalDateTime.now());

                    if (proforma.getStatut() == null) {
                        proforma.setStatut(StatutProforma.BROUILLON);
                    }

                    List<LigneFactureProforma> tempLignes = proforma.getLignesFactureProforma();
                    if (tempLignes != null) {
                        for (LigneFactureProforma ligne : tempLignes) {
                            calculateLigneTotal(ligne);
                        }
                        calculateProformaTotals(proforma);
                    }

                    return proformaRepository.save(proforma)
                            .flatMap(savedProforma -> {
                                if (tempLignes == null || tempLignes.isEmpty()) {
                                    return Mono.just(savedProforma);
                                }
                                
                                return Flux.fromIterable(tempLignes)
                                        .map(ligne -> {
                                            ligne.setIdProformaInvoice(savedProforma.getIdProformaInvoice());
                                            ligne.setOrganizationId(orgId);
                                            return ligne;
                                        })
                                        .flatMap(ligneProformaRepository::save)
                                        .collectList()
                                        .map(savedLignes -> {
                                            savedProforma.setLignesFactureProforma(savedLignes);
                                            return savedProforma;
                                        });
                            });
                })
                .map(proformaMapper::toResponse);
    }

    private void calculateLigneTotal(LigneFactureProforma ligne) {
        BigDecimal quantite = new BigDecimal(ligne.getQuantite());
        BigDecimal prixU = ligne.getPrixUnitaire() != null ? ligne.getPrixUnitaire() : BigDecimal.ZERO;
        
        BigDecimal total = prixU.multiply(quantite);
        
        if (ligne.getRemiseMontant() != null && ligne.getRemiseMontant().compareTo(BigDecimal.ZERO) > 0) {
            total = total.subtract(ligne.getRemiseMontant());
        } else if (ligne.getRemisePourcentage() != null && ligne.getRemisePourcentage().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal remise = total.multiply(ligne.getRemisePourcentage()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            total = total.subtract(remise);
        }
        
        ligne.setMontantTotal(total);
    }

    private void calculateProformaTotals(FactureProforma proforma) {
        BigDecimal totalHT = proforma.getLignesFactureProforma().stream()
                .map(l -> l.getMontantTotal() != null ? l.getMontantTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        proforma.setMontantHT(totalHT);
        
        BigDecimal totalTVA = BigDecimal.ZERO;
        for (LigneFactureProforma ligne : proforma.getLignesFactureProforma()) {
            if (ligne.getTauxTva() != null && ligne.getTauxTva().compareTo(BigDecimal.ZERO) > 0) {
                totalTVA = totalTVA.add(ligne.getMontantTotal().multiply(ligne.getTauxTva()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
            }
        }
        
        proforma.setMontantTVA(totalTVA);
        BigDecimal ttc = totalHT.add(totalTVA);
        
        if (proforma.getRemiseGlobaleMontant() != null && proforma.getRemiseGlobaleMontant().compareTo(BigDecimal.ZERO) > 0) {
            ttc = ttc.subtract(proforma.getRemiseGlobaleMontant());
        } else if (proforma.getRemiseGlobalePourcentage() != null && proforma.getRemiseGlobalePourcentage().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal remise = ttc.multiply(proforma.getRemiseGlobalePourcentage()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            ttc = ttc.subtract(remise);
        }
        
        proforma.setMontantTTC(ttc);
        proforma.setMontantTotal(ttc);
        proforma.setFinalAmount(ttc);
    }

    @Transactional(readOnly = true)
    public Mono<ProformaInvoiceResponse> getProformaById(UUID id) {
        return proformaRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Facture proforma non trouvée: " + id)))
                .flatMap(proforma -> ligneProformaRepository.findByIdProformaInvoice(id)
                        .collectList()
                        .map(lignes -> {
                            proforma.setLignesFactureProforma(lignes);
                            return proforma;
                        }))
                .map(proformaMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<ProformaInvoiceResponse> getAllProformas() {
        return proformaRepository.findAll()
                .flatMap(proforma -> ligneProformaRepository.findByIdProformaInvoice(proforma.getIdProformaInvoice())
                        .collectList()
                        .map(lignes -> {
                            proforma.setLignesFactureProforma(lignes);
                            return proforma;
                        }))
                .map(proformaMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<ProformaInvoiceResponse> getProformasByClient(UUID idClient) {
        return proformaRepository.findByIdClient(idClient)
                .flatMap(proforma -> ligneProformaRepository.findByIdProformaInvoice(proforma.getIdProformaInvoice())
                        .collectList()
                        .map(lignes -> {
                            proforma.setLignesFactureProforma(lignes);
                            return proforma;
                        }))
                .map(proformaMapper::toResponse);
    }

    @Transactional
    public Mono<Void> deleteProforma(UUID id) {
        return proformaRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new IllegalArgumentException("Facture proforma non trouvée: " + id));
                    }
                    return ligneProformaRepository.findByIdProformaInvoice(id)
                            .flatMap(ligneProformaRepository::delete)
                            .then(proformaRepository.deleteById(id));
                });
    }

    @Transactional
    public Mono<ProformaInvoiceResponse> updateStatut(UUID id, StatutProforma nouveauStatut) {
        return proformaRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Facture proforma non trouvée: " + id)))
                .flatMap(proforma -> {
                    proforma.setStatut(nouveauStatut);
                    proforma.setUpdatedAt(LocalDateTime.now());
                    
                    if (nouveauStatut == StatutProforma.ACCEPTE) {
                        proforma.setDateAcceptation(LocalDateTime.now());
                    } else if (nouveauStatut == StatutProforma.REFUSE) {
                        proforma.setDateRefus(LocalDateTime.now());
                    }
                    
                    return proformaRepository.save(proforma)
                            .flatMap(savedProforma -> ligneProformaRepository.findByIdProformaInvoice(id)
                                    .collectList()
                                    .map(lignes -> {
                                        savedProforma.setLignesFactureProforma(lignes);
                                        return savedProforma;
                                    }));
                })
                .map(proformaMapper::toResponse);
    }
}
