package com.example.account.modules.facturation.service;

import com.example.account.modules.facturation.dto.request.ProformaInvoiceRequest;
import com.example.account.modules.facturation.dto.response.ProformaInvoiceResponse;
import com.example.account.modules.facturation.mapper.FactureProformaMapper;
import com.example.account.modules.facturation.model.entity.FactureProforma;
import com.example.account.modules.facturation.model.enums.StatutProforma;
import com.example.account.modules.facturation.repository.FactureProformaRepository;
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
public class FactureProformaService {

    private final FactureProformaRepository proformaRepository;
    private final FactureProformaMapper proformaMapper;
    private final org.springframework.data.r2dbc.core.R2dbcEntityTemplate entityTemplate;

    @Transactional
    public Mono<ProformaInvoiceResponse> createProforma(ProformaInvoiceRequest request) {
        log.info("Création d'une nouvelle facture proforma pour le client: {}", request.getIdClient());

        FactureProforma proforma = proformaMapper.toEntity(request);
        if (proforma.getIdFactureProforma() == null) {
            proforma.setIdFactureProforma(UUID.randomUUID());
        }
        proforma.setDateCreation(LocalDateTime.now());

        if (proforma.getStatut() == null) {
            proforma.setStatut(StatutProforma.BROUILLON);
        }

        return entityTemplate.insert(proforma)
                .map(proformaMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Mono<ProformaInvoiceResponse> getProformaById(UUID id) {
        log.info("Récupération de la facture proforma: {}", id);
        return proformaRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Facture proforma non trouvée: " + id)))
                .map(proformaMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<ProformaInvoiceResponse> getAllProformas() {
        log.info("Récupération de toutes les factures proforma");
        return proformaRepository.findAll()
                .map(proformaMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<ProformaInvoiceResponse> getProformasByClient(UUID idClient) {
        log.info("Récupération des factures proforma du client: {}", idClient);
        return proformaRepository.findByIdClient(idClient)
                .map(proformaMapper::toResponse);
    }

    @Transactional
    public Mono<Void> deleteProforma(UUID id) {
        log.info("Suppression de la facture proforma: {}", id);
        return proformaRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new IllegalArgumentException("Facture proforma non trouvée: " + id));
                    }
                    return proformaRepository.deleteById(id);
                });
    }

    @Transactional
    public Mono<ProformaInvoiceResponse> updateStatut(UUID id, StatutProforma nouveauStatut) {
        log.info("Mise à jour du statut de la facture proforma: {} vers {}", id, nouveauStatut);
        return proformaRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Facture proforma non trouvée: " + id)))
                .flatMap(proforma -> {
                    proforma.setStatut(nouveauStatut);
                    if (nouveauStatut == StatutProforma.ACCEPTE) {
                        proforma.setDateAcceptation(LocalDateTime.now());
                    } else if (nouveauStatut == StatutProforma.REFUSE) {
                        proforma.setDateRefus(LocalDateTime.now());
                    }
                    return proformaRepository.save(proforma);
                })
                .map(proformaMapper::toResponse);
    }

    @Transactional
    public Mono<ProformaInvoiceResponse> updateFactureProforma(UUID id, ProformaInvoiceRequest request) {
        log.info("Mise à jour de la facture proforma: {}", id);
        return proformaRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Facture proforma non trouvée: " + id)))
                .flatMap(proforma -> {
                    proformaMapper.updateProformaFromDTO(request, proforma);
                    return proformaRepository.save(proforma);
                })
                .map(proformaMapper::toResponse);
    }
}
