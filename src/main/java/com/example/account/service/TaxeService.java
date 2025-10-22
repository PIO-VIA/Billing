package com.example.account.service;

import com.example.account.dto.request.TaxeCreateRequest;
import com.example.account.dto.request.TaxeUpdateRequest;
import com.example.account.dto.response.TaxeResponse;
import com.example.account.mapper.TaxeMapper;
import com.example.account.model.entity.Taxes;
import com.example.account.repository.TaxesRepository;
import com.example.account.service.producer.TaxeEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaxeService {

    private final TaxesRepository taxesRepository;
    private final TaxeMapper taxeMapper;
    private final TaxeEventProducer taxeEventProducer;

    @Transactional
    public TaxeResponse createTaxe(TaxeCreateRequest request) {
        log.info("Création d'une nouvelle taxe: {}", request.getNomTaxe());

        // Vérifications
        if (taxesRepository.existsByNomTaxe(request.getNomTaxe())) {
            throw new IllegalArgumentException("Une taxe avec ce nom existe déjà");
        }

        // Créer et sauvegarder la taxe
        Taxes taxe = taxeMapper.toEntity(request);
        Taxes savedTaxe = taxesRepository.save(taxe);
        TaxeResponse response = taxeMapper.toResponse(savedTaxe);

        // Publier l'événement
        taxeEventProducer.publishTaxeCreated(response);

        log.info("Taxe créée avec succès: {}", savedTaxe.getIdTaxe());
        return response;
    }

    @Transactional
    public TaxeResponse updateTaxe(UUID taxeId, TaxeUpdateRequest request) {
        log.info("Mise à jour de la taxe: {}", taxeId);

        Taxes taxe = taxesRepository.findById(taxeId)
                .orElseThrow(() -> new IllegalArgumentException("Taxe non trouvée: " + taxeId));

        // Mise à jour
        taxeMapper.updateEntityFromRequest(request, taxe);
        Taxes updatedTaxe = taxesRepository.save(taxe);
        TaxeResponse response = taxeMapper.toResponse(updatedTaxe);

        // Publier l'événement
        taxeEventProducer.publishTaxeUpdated(response);

        log.info("Taxe mise à jour avec succès: {}", taxeId);
        return response;
    }

    @Transactional(readOnly = true)
    public TaxeResponse getTaxeById(UUID taxeId) {
        log.info("Récupération de la taxe: {}", taxeId);

        Taxes taxe = taxesRepository.findById(taxeId)
                .orElseThrow(() -> new IllegalArgumentException("Taxe non trouvée: " + taxeId));

        return taxeMapper.toResponse(taxe);
    }

    @Transactional(readOnly = true)
    public TaxeResponse getTaxeByNom(String nomTaxe) {
        log.info("Récupération de la taxe par nom: {}", nomTaxe);

        Taxes taxe = taxesRepository.findByNomTaxe(nomTaxe)
                .orElseThrow(() -> new IllegalArgumentException("Taxe non trouvée avec nom: " + nomTaxe));

        return taxeMapper.toResponse(taxe);
    }

    @Transactional(readOnly = true)
    public List<TaxeResponse> getAllTaxes() {
        log.info("Récupération de toutes les taxes");
        List<Taxes> taxes = taxesRepository.findAll();
        return taxeMapper.toResponseList(taxes);
    }

    @Transactional(readOnly = true)
    public Page<TaxeResponse> getAllTaxes(Pageable pageable) {
        log.info("Récupération de toutes les taxes (paginée)");
        Page<Taxes> taxes = taxesRepository.findAll(pageable);
        return taxes.map(taxeMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<TaxeResponse> getActiveTaxes() {
        log.info("Récupération des taxes actives");
        List<Taxes> taxes = taxesRepository.findAllActiveTaxes();
        return taxeMapper.toResponseList(taxes);
    }

    @Transactional(readOnly = true)
    public List<TaxeResponse> getTaxesByType(String typeTaxe) {
        log.info("Récupération des taxes par type: {}", typeTaxe);
        List<Taxes> taxes = taxesRepository.findByTypeTaxe(typeTaxe);
        return taxeMapper.toResponseList(taxes);
    }

    @Transactional(readOnly = true)
    public List<TaxeResponse> getActiveTaxesByType(String typeTaxe) {
        log.info("Récupération des taxes actives par type: {}", typeTaxe);
        List<Taxes> taxes = taxesRepository.findActiveByTypeTaxe(typeTaxe);
        return taxeMapper.toResponseList(taxes);
    }

    @Transactional(readOnly = true)
    public List<TaxeResponse> getTaxesByPorte(String porteTaxe) {
        log.info("Récupération des taxes par portée: {}", porteTaxe);
        List<Taxes> taxes = taxesRepository.findByPorteTaxe(porteTaxe);
        return taxeMapper.toResponseList(taxes);
    }

    @Transactional(readOnly = true)
    public List<TaxeResponse> getTaxesByPositionFiscale(String positionFiscale) {
        log.info("Récupération des taxes par position fiscale: {}", positionFiscale);
        List<Taxes> taxes = taxesRepository.findByPositionFiscale(positionFiscale);
        return taxeMapper.toResponseList(taxes);
    }

    @Transactional(readOnly = true)
    public List<TaxeResponse> getTaxesByCalculRange(BigDecimal minTaux, BigDecimal maxTaux) {
        log.info("Récupération des taxes par plage de calcul: {} - {}", minTaux, maxTaux);
        List<Taxes> taxes = taxesRepository.findByCalculTaxeBetween(minTaux, maxTaux);
        return taxeMapper.toResponseList(taxes);
    }

    @Transactional(readOnly = true)
    public List<TaxeResponse> getTaxesByMontantRange(BigDecimal minMontant, BigDecimal maxMontant) {
        log.info("Récupération des taxes par plage de montant: {} - {}", minMontant, maxMontant);
        List<Taxes> taxes = taxesRepository.findByMontantBetween(minMontant, maxMontant);
        return taxeMapper.toResponseList(taxes);
    }

    @Transactional
    public void deleteTaxe(UUID taxeId) {
        log.info("Suppression de la taxe: {}", taxeId);

        if (!taxesRepository.existsById(taxeId)) {
            throw new IllegalArgumentException("Taxe non trouvée: " + taxeId);
        }

        taxesRepository.deleteById(taxeId);

        // Publier l'événement
        taxeEventProducer.publishTaxeDeleted(taxeId);

        log.info("Taxe supprimée avec succès: {}", taxeId);
    }

    @Transactional
    public TaxeResponse activerTaxe(UUID taxeId) {
        log.info("Activation de la taxe: {}", taxeId);

        Taxes taxe = taxesRepository.findById(taxeId)
                .orElseThrow(() -> new IllegalArgumentException("Taxe non trouvée: " + taxeId));

        taxe.setActif(true);
        Taxes updatedTaxe = taxesRepository.save(taxe);
        TaxeResponse response = taxeMapper.toResponse(updatedTaxe);

        // Publier l'événement
        taxeEventProducer.publishTaxeUpdated(response);

        log.info("Taxe activée avec succès: {}", taxeId);
        return response;
    }

    @Transactional
    public TaxeResponse desactiverTaxe(UUID taxeId) {
        log.info("Désactivation de la taxe: {}", taxeId);

        Taxes taxe = taxesRepository.findById(taxeId)
                .orElseThrow(() -> new IllegalArgumentException("Taxe non trouvée: " + taxeId));

        taxe.setActif(false);
        Taxes updatedTaxe = taxesRepository.save(taxe);
        TaxeResponse response = taxeMapper.toResponse(updatedTaxe);

        // Publier l'événement
        taxeEventProducer.publishTaxeUpdated(response);

        log.info("Taxe désactivée avec succès: {}", taxeId);
        return response;
    }

    @Transactional(readOnly = true)
    public Long countActiveTaxes() {
        return taxesRepository.countActiveTaxes();
    }

    @Transactional(readOnly = true)
    public Long countByType(String typeTaxe) {
        return taxesRepository.countByTypeTaxe(typeTaxe);
    }
}
