package com.example.account.modules.facturation.service;

import com.example.account.modules.facturation.dto.request.TaxeCreateRequest;
import com.example.account.modules.facturation.dto.request.TaxeUpdateRequest;
import com.example.account.modules.facturation.dto.response.TaxeResponse;
import com.example.account.modules.facturation.mapper.TaxeMapper;
import com.example.account.modules.facturation.model.entity.Taxes;
import com.example.account.modules.facturation.repository.TaxesRepository;
import com.example.account.modules.facturation.service.producer.TaxeEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaxeService {

    private final TaxesRepository taxesRepository;
    private final TaxeMapper taxeMapper;
    private final TaxeEventProducer taxeEventProducer;
    private final R2dbcEntityTemplate entityTemplate;

    @Transactional
    public Mono<TaxeResponse> createTaxe(TaxeCreateRequest request) {
        log.info("Création d'une nouvelle taxe: {}", request.getNomTaxe());

        return taxesRepository.existsByNomTaxe(request.getNomTaxe())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Une taxe avec ce nom existe déjà"));
                    }
                    Taxes taxe = taxeMapper.toEntity(request);
                    if (taxe.getIdTaxe() == null) {
                        taxe.setIdTaxe(UUID.randomUUID());
                    }
                    return taxesRepository.save(taxe);
                })
                .map(savedTaxe -> {
                    TaxeResponse response = taxeMapper.toResponse(savedTaxe);
                    taxeEventProducer.publishTaxeCreated(response);
                    log.info("Taxe créée avec succès: {}", savedTaxe.getIdTaxe());
                    return response;
                });
    }

    @Transactional
    public Mono<TaxeResponse> updateTaxe(UUID taxeId, TaxeUpdateRequest request) {
        log.info("Mise à jour de la taxe: {}", taxeId);

        return taxesRepository.findById(taxeId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Taxe non trouvée: " + taxeId)))
                .flatMap(taxe -> {
                    taxeMapper.updateEntityFromRequest(request, taxe);
                    return taxesRepository.save(taxe);
                })
                .map(updatedTaxe -> {
                    TaxeResponse response = taxeMapper.toResponse(updatedTaxe);
                    taxeEventProducer.publishTaxeUpdated(response);
                    log.info("Taxe mise à jour avec succès: {}", taxeId);
                    return response;
                });
    }

    @Transactional(readOnly = true)
    public Mono<TaxeResponse> getTaxeById(UUID taxeId) {
        log.info("Récupération de la taxe: {}", taxeId);
        return taxesRepository.findById(taxeId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Taxe non trouvée: " + taxeId)))
                .map(taxeMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Mono<TaxeResponse> getTaxeByNom(String nomTaxe) {
        log.info("Récupération de la taxe par nom: {}", nomTaxe);
        return taxesRepository.findByNomTaxe(nomTaxe)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Taxe non trouvée avec nom: " + nomTaxe)))
                .map(taxeMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<TaxeResponse> getAllTaxes() {
        log.info("Récupération de toutes les taxes");
        return taxesRepository.findAll()
                .map(taxeMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<TaxeResponse> getAllTaxes(Pageable pageable) {
        log.info("Récupération de toutes les taxes (paginée)");
        return taxesRepository.findAll()
                .skip(pageable.getOffset())
                .take(pageable.getPageSize())
                .map(taxeMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<TaxeResponse> getActiveTaxes() {
        log.info("Récupération des taxes actives");
        return taxesRepository.findAllActiveTaxes()
                .map(taxeMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<TaxeResponse> getTaxesByType(String typeTaxe) {
        log.info("Récupération des taxes par type: {}", typeTaxe);
        return taxesRepository.findByTypeTaxe(typeTaxe)
                .map(taxeMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<TaxeResponse> getActiveTaxesByType(String typeTaxe) {
        log.info("Récupération des taxes actives par type: {}", typeTaxe);
        return taxesRepository.findActiveByTypeTaxe(typeTaxe)
                .map(taxeMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<TaxeResponse> getTaxesByPorte(String porteTaxe) {
        log.info("Récupération des taxes par portée: {}", porteTaxe);
        return taxesRepository.findByPorteTaxe(porteTaxe)
                .map(taxeMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<TaxeResponse> getTaxesByPositionFiscale(String positionFiscale) {
        log.info("Récupération des taxes par position fiscale: {}", positionFiscale);
        return taxesRepository.findByPositionFiscale(positionFiscale)
                .map(taxeMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<TaxeResponse> getTaxesByCalculRange(BigDecimal minTaux, BigDecimal maxTaux) {
        log.info("Récupération des taxes par plage de calcul: {} - {}", minTaux, maxTaux);
        return taxesRepository.findByCalculTaxeBetween(minTaux, maxTaux)
                .map(taxeMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Flux<TaxeResponse> getTaxesByMontantRange(BigDecimal minMontant, BigDecimal maxMontant) {
        log.info("Récupération des taxes par plage de montant: {} - {}", minMontant, maxMontant);
        return taxesRepository.findByMontantBetween(minMontant, maxMontant)
                .map(taxeMapper::toResponse);
    }

    @Transactional
    public Mono<Void> deleteTaxe(UUID taxeId) {
        log.info("Suppression de la taxe: {}", taxeId);
        return taxesRepository.existsById(taxeId)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new IllegalArgumentException("Taxe non trouvée: " + taxeId));
                    }
                    return taxesRepository.deleteById(taxeId)
                            .then(Mono.fromRunnable(() -> taxeEventProducer.publishTaxeDeleted(taxeId)));
                });
    }

    @Transactional
    public Mono<TaxeResponse> activerTaxe(UUID taxeId) {
        log.info("Activation de la taxe: {}", taxeId);
        return taxesRepository.findById(taxeId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Taxe non trouvée: " + taxeId)))
                .flatMap(taxe -> {
                    taxe.setActif(true);
                    return taxesRepository.save(taxe);
                })
                .map(updatedTaxe -> {
                    TaxeResponse response = taxeMapper.toResponse(updatedTaxe);
                    taxeEventProducer.publishTaxeUpdated(response);
                    log.info("Taxe activée avec succès: {}", taxeId);
                    return response;
                });
    }

    @Transactional
    public Mono<TaxeResponse> desactiverTaxe(UUID taxeId) {
        log.info("Désactivation de la taxe: {}", taxeId);
        return taxesRepository.findById(taxeId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Taxe non trouvée: " + taxeId)))
                .flatMap(taxe -> {
                    taxe.setActif(false);
                    return taxesRepository.save(taxe);
                })
                .map(updatedTaxe -> {
                    TaxeResponse response = taxeMapper.toResponse(updatedTaxe);
                    taxeEventProducer.publishTaxeUpdated(response);
                    log.info("Taxe désactivée avec succès: {}", taxeId);
                    return response;
                });
    }

    @Transactional(readOnly = true)
    public Mono<Long> countActiveTaxes() {
        return taxesRepository.countActiveTaxes();
    }

    @Transactional(readOnly = true)
    public Mono<Long> countByType(String typeTaxe) {
        return taxesRepository.countByTypeTaxe(typeTaxe);
    }
}
